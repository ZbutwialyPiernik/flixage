package com.zbutwialypiernik.flixage.config;

import com.zbutwialypiernik.flixage.entity.file.AudioFileEntity;
import com.zbutwialypiernik.flixage.entity.file.ImageFileEntity;
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
            // Don't change it to lambda, otherwise java will lose generic type info
            registry.addConverter(new Converter<AudioFileEntity, String>() {
                @Override
                public String convert(AudioFileEntity source) {
                    if (source.getFileId() == null) {
                        source.setFileId(UUID.randomUUID().toString());
                    }

                    return "./storage/track/" + source.getFileId() + "." + source.getExtension();
                }
            });

            // Don't change it to lambda, otherwise java will lose generic type info
            registry.addConverter(new Converter<ImageFileEntity, String>() {
                @Override
                public String convert(ImageFileEntity source) {
                    if (source.getFileId() == null) {
                        source.setFileId(UUID.randomUUID().toString());
                    }

                    return "./storage/image/" + source.getFileId() + "." + source.getExtension();
                }
            });
        };
    }

    @Bean
    public FileSystemResourceLoader fsResourceLoader() {
        return new FileSystemResourceLoader(filesystemRoot().getAbsolutePath());
    }

}
