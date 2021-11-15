package com.vneuron.springplugins.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.vneuron.springplugins.SpringPluginsApplication;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.vneuron.springplugins.classloader.PluginClassloader;

@Controller
@RequestMapping("/plugins")
public class UploadPluginController {


    /**
     * This POST will trigger a file copy to the filesystem and an immediate refresh of the PluginClassLoader.
     */
    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        File targetFile = new File("plugins" + File.separator + file.getOriginalFilename());
        OutputStream fileOutputStream = new FileOutputStream(targetFile);

        FileCopyUtils.copy(file.getInputStream(), fileOutputStream);

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl.getParent() instanceof PluginClassloader) {
            ((PluginClassloader)cl.getParent()).init();
        }
        // After uploading a plugin, restart, so that we can actually read the new plugin and use it, hopefully...
        SpringPluginsApplication.restart();
        return "ok";
    }

    @GetMapping("/list")
    @ResponseBody
    public List<String> listPlugins() {
        return Arrays.stream(Objects.requireNonNull(new File("plugins").listFiles())).map(File::getName).collect(Collectors.toList());
    }

}