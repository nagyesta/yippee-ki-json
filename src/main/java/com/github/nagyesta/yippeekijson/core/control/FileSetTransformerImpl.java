package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FileSetTransformerImpl implements FileSetTransformer {

    private static final String UNIX_SEPARATOR = "/";
    private static final String EMPTY = "";

    @Override
    public Map<File, File> transformToFilePairs(@NonNull final RunConfig runConfig) {
        final File inputAsFile = runConfig.getInputAsFile();
        final List<File> toTransform = compileInputList(runConfig, inputAsFile);

        if (!runConfig.isOutputFileDirectory()) {
            Assert.isTrue(toTransform.size() == 1, "As output is file, we can only process 1 input at once.");
        }
        return formFilePairs(toTransform, runConfig);
    }

    private Map<File, File> formFilePairs(final List<File> toTransform, final RunConfig runConfig) {
        final String unixInputDirectory = getUnixInputDirectory(runConfig.getInputAsFile());

        final File outputAsFile = runConfig.getOutputAsFile();
        final boolean treatOutputAsDirectory = runConfig.isOutputFileDirectory();

        return toTransform.stream()
                .map(file -> {
                    if (treatOutputAsDirectory) {
                        final String relativePath = relativeToInputDirectory(unixInputDirectory, file);
                        final File out = concatRelativeToOutputDirectory(outputAsFile, relativePath);
                        return Map.entry(file, out);
                    } else {
                        return Map.entry(file, outputAsFile.getAbsoluteFile());
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private File concatRelativeToOutputDirectory(final File outputAsFile, final String relativePath) {
        final String fullPath = FilenameUtils.normalizeNoEndSeparator(outputAsFile.getAbsolutePath(), true)
                + UNIX_SEPARATOR + relativePath;
        return new File(FilenameUtils.separatorsToSystem(fullPath));
    }

    private String relativeToInputDirectory(final String unixInputDirectory, final File file) {
        final String normalized = FilenameUtils.normalize(file.getAbsolutePath(), true);
        return normalized.replaceFirst(Pattern.quote(unixInputDirectory), EMPTY);
    }

    private String getUnixInputDirectory(final File inputAsFile) {
        if (inputAsFile.isDirectory()) {
            return FilenameUtils.normalizeNoEndSeparator(inputAsFile.getAbsolutePath(), true) + UNIX_SEPARATOR;
        } else {
            return FilenameUtils.normalizeNoEndSeparator(inputAsFile.getParent(), true) + UNIX_SEPARATOR;
        }
    }

    private List<File> compileInputList(final RunConfig runConfig, final File inputAsFile) {
        final List<File> toTransform;
        if (inputAsFile.isDirectory()) {
            toTransform = filterDirectory(runConfig, inputAsFile);
        } else {
            toTransform = Collections.singletonList(inputAsFile);
        }
        return toTransform;
    }

    private List<File> filterDirectory(final RunConfig runConfig, final File inputAsFile) {
        List<File> toTransform;
        final File[] files = inputAsFile.listFiles(runConfig.getWildcardFileFilter());
        if (files != null && files.length > 0) {
            toTransform = Arrays.asList(files);
        } else {
            toTransform = Collections.emptyList();
            log.warn("No file matched the include-exclude filters.");
        }
        return toTransform;
    }
}
