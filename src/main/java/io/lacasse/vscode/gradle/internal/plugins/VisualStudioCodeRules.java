package io.lacasse.vscode.gradle.internal.plugins;

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
import org.gradle.nativeplatform.NativeBinarySpec;
import org.gradle.nativeplatform.NativeExecutableBinarySpec;
import org.gradle.nativeplatform.NativeExecutableSpec;
import org.gradle.nativeplatform.SharedLibraryBinarySpec;
import org.gradle.nativeplatform.StaticLibraryBinarySpec;
import org.gradle.platform.base.ComponentSpecContainer;
import org.gradle.platform.base.VariantComponentSpec;

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
    void populate(VisualStudioCodeExtension visualStudioCode, ComponentSpecContainer components, ServiceRegistry serviceRegistry) {
        ProviderFactory providerFactory = serviceRegistry.get(ProviderFactory.class);

        components.withType(VariantComponentSpec.class).forEach(component -> {
            component.getBinaries().withType(NativeBinarySpec.class).forEach(binary -> {
                visualStudioCode.getProject().cppConfiguration(binary.getName(), (it) -> it.configureFromBinary(binary));
                // TODO: default should consider buildable flag
                if (binary instanceof SharedLibraryBinarySpec) {
                    visualStudioCode.getProject().buildTask("Build " + binary.getName(), providerFactory.provider(() -> binary.getBuildTask()), binary.getBuildType().getName().equals("debug"));
                } else if (binary instanceof StaticLibraryBinarySpec) {
                    visualStudioCode.getProject().buildTask("Build " + binary.getName(), providerFactory.provider(() -> binary.getBuildTask()));
                } else if (binary instanceof NativeExecutableBinarySpec) {
                    visualStudioCode.getProject().buildTask("Build " + binary.getName(), providerFactory.provider(() -> binary.getBuildTask()), binary.getBuildType().getName().equals("debug"));
                }
            });
        });
    }
}
