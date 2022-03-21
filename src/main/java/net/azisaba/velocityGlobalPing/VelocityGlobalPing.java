package net.azisaba.velocityGlobalPing;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.azisaba.velocityredisbridge.VelocityRedisBridge;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VelocityGlobalPing {
    private final List<String> servers = new ArrayList<>();
    private final ProxyServer server;

    @Inject
    public VelocityGlobalPing(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        try {
            logger.info("Loading config from " + dataDirectory.resolve("config.yml"));
            //noinspection UnstableApiUsage
            List<String> list = YAMLConfigurationLoader.builder()
                    .setPath(dataDirectory.resolve("config.yml"))
                    .build()
                    .load()
                    .getNode("servers")
                    .getList(TypeToken.of(String.class))
                    .stream()
                    .map(String::toLowerCase)
                    .toList();
            servers.addAll(list);
            logger.info("Loaded " + servers.size() + " servers");
        } catch (IOException e) {
            logger.warn("Failed to load config", e);
        } catch (ObjectMappingException e) {
            logger.warn("Failed to cast value", e);
        }
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent e) {
        e.getConnection().getVirtualHost().map(InetSocketAddress::getHostName).ifPresent(hostName -> {
            if (servers.contains(hostName.toLowerCase(Locale.ROOT))) {
                ServerPing.Builder builder = e.getPing().asBuilder();
                this.server.getConfiguration().getFavicon().ifPresent(builder::favicon);
                builder.clearSamplePlayers();
                builder.onlinePlayers(VelocityRedisBridge.getApi().getAllPlayerInfo().size());
                int pv = e.getConnection().getProtocolVersion().getProtocol();
                if (pv < 0) pv = ProtocolVersion.MINECRAFT_1_7_2.getProtocol();
                String versionName = "Velocity " + ProtocolVersion.MINIMUM_VERSION.getVersionIntroducedIn() + "-" + ProtocolVersion.MAXIMUM_VERSION.getMostRecentSupportedVersion();
                builder.version(new ServerPing.Version(pv, versionName));
                builder.description(this.server.getConfiguration().getMotd());
                builder.maximumPlayers(this.server.getConfiguration().getShowMaxPlayers());
                e.setPing(builder.build());
            }
        });
    }
}
