package io.lacasse.vscode.gradle.internal.plugins;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.lacasse.vscode.gradle.VisualStudioCodeExtension;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppExecutable;
import org.gradle.language.cpp.CppLibrary;
import org.gradle.language.cpp.CppSharedLibrary;
import org.gradle.language.cpp.CppStaticLibrary;
import org.gradle.model.Each;
import org.gradle.model.Model;
import org.gradle.model.Mutate;
import org.gradle.model.RuleSource;
import org.gradle.nativeplatform.BuildTypeContainer;
import org.gradle.nativeplatform.NativeBinarySpec;
import org.gradle.nativeplatform.NativeExecutableBinarySpec;
import org.gradle.nativeplatform.NativeExecutableSpec;
import org.gradle.nativeplatform.SharedLibraryBinarySpec;
import org.gradle.nativeplatform.StaticLibraryBinarySpec;
import org.gradle.platform.base.ComponentSpecContainer;
import org.gradle.platform.base.VariantComponentSpec;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class VisualStudioCodeRules extends RuleSource {
    @Model
    VisualStudioCodeExtension visualStudioCode(ExtensionContainer extensions) {
        return (VisualStudioCodeExtension) extensions.getByName("visualStudioCode");
    }

    @Mutate
    void realizeExtensionWithTaskContainer(TaskContainer tasks, VisualStudioCodeExtension visualStudioCode) {
        // do nothing
    }

    @Mutate
    void populate(VisualStudioCodeExtension visualStudioCode, ComponentSpecContainer components, ServiceRegistry serviceRegistry, BuildTypeContainer buildTypes) {
        ProviderFactory providerFactory = serviceRegistry.get(ProviderFactory.class);

        components.withType(VariantComponentSpec.class).forEach(component -> {
            component.getBinaries().withType(NativeBinarySpec.class).forEach(binary -> {
                visualStudioCode.getProject().cppConfiguration(binary.getName(), (it) -> it.configureFromBinary(binary));
                // TODO: default should consider buildable flag
                if (binary.isBuildable()) {
                    if (binary instanceof SharedLibraryBinarySpec) {
                        visualStudioCode.getProject().buildTask("Build " + binary.getName(), providerFactory.provider(() -> binary.getBuildTask()), isDefault(binary, buildTypes, Collections.emptyList()));
                    } else if (binary instanceof StaticLibraryBinarySpec) {
                        visualStudioCode.getProject().buildTask("Build " + binary.getName(), providerFactory.provider(() -> binary.getBuildTask()));
                    } else if (binary instanceof NativeExecutableBinarySpec) {
                        visualStudioCode.getProject().buildTask("Build " + binary.getName(), providerFactory.provider(() -> binary.getBuildTask()), isDefault(binary, buildTypes,
                                Lists.newArrayList(Iterators.filter(component.getBinaries().withType(NativeBinarySpec.class).iterator(), NativeBinarySpec::isBuildable))));
                    }
                }
            });
        });
    }

    private static boolean isDefault(NativeBinarySpec binary, BuildTypeContainer buildTypes, List<NativeBinarySpec> buildableBinaries) {
        if (binary.getBuildType().getName().equals("debug")) {
            return true;
        } else if (buildTypes.size() == 1) {
            return true;
        } else if (buildTypes.iterator().next().getName().equals(binary.getBuildType().getName())) {
            return true;
        } else if (binary.getName().equals(buildableBinaries.iterator().next().getName())) {
            return true;
        }
        return false;
    }
}
