package com.dre.brewery;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShardConfigSync {

	// Modified code from BConfig#loadConfigFile
	public static FileConfiguration loadApiConfigFile() {
		File file = new File(P.p.getDataFolder(), "api.yml");
		if (!file.exists()) {
			P.p.errorLog("api.yml does not exist. Make it immediately and fill out the sharding synchronization api information.");
			return null;
		}

		try {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			if (cfg.contains("CONFIG_ENDPOINT")) {
				return cfg;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		P.p.errorLog("Could not read file api.yml, please make sure the file is in valid yml format (correct spaces etc.)");
		return null;
	}

	public static YamlConfiguration loadConfigFromServer(FileConfiguration requestConfig) {
		try {
			final OkHttpClient client = new OkHttpClient();

			final Request request = new Request.Builder()
				.url(requestConfig.getString("CONFIG_ENDPOINT"))
				.header(requestConfig.getString("AUTH_HEADER_KEY"), requestConfig.getString("AUTH_HEADER_VALUE"))
				.header(requestConfig.getString("REQUEST_HEADER_KEY"), requestConfig.getString("AUTH_HEADER_VALUE"))
				.build();

			try (Response response = client.newCall(request).execute()) {
				if(response.isSuccessful() && response.body() != null) {
					try(InputStream stream = response.body().byteStream()) {
						return YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
					}
				} else {
					return null;
				}
			}
		}catch(IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
