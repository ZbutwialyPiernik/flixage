package com.zbutwialypiernik.flixage.config;

import com.zbutwialypiernik.flixage.entity.Thumbnail;
import com.zbutwialypiernik.flixage.entity.Track;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.config.FilesystemStoreConfigurer;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.io.File;
import java.util.UUID;

@Configuration
@EnableFilesystemStores
public class FileStorageConfiguration {

    File filesystemRoot() {
        return new File("./storage");
    }

    @Bean
    public FilesystemStoreConfigurer configurer() {
        return registry -> {
            registry.addConverter(new Converter<Track, String>() {
                @Override
                public String convert(Track source) {
                    if (source.getFileId() == null) {
                        source.setFileId(UUID.randomUUID().toString());
                    }

                    return "./storage/track/" + source.getFileId();
                }
            });

            registry.addConverter(new Converter<Thumbnail, String>() {
                @Override
                public String convert(Thumbnail source) {
                    if (source.getFileId() == null) {
                        source.setFileId(UUID.randomUUID().toString());
                    }

                    return "./storage/image/" + source.getFileId();
                }
            });
        };
    }

    @Bean
    public FileSystemResourceLoader fsResourceLoader() {
        return new FileSystemResourceLoader(filesystemRoot().getAbsolutePath());
    }

}
