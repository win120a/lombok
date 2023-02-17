package lombok.eclipse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public abstract class SetupTest extends TestWatcher {

    private IJavaProject javaProject;
    protected IPackageFragment packageFragment;
    private IWorkspace workspace;
    protected File root;

    public IJavaProject getJavaProject() {
        return javaProject;
    }

    public IPackageFragment getPackageFragment() {
        return packageFragment;
    }

    public IWorkspace getWorkspace() {
        return workspace;
    }

    @Override
    protected void starting(Description description) {
        String category = description.getTestClass().getSimpleName().toLowerCase().replaceAll("test$", "");
        String testname = description.getMethodName();

        root = new File("test/eclipse/resource/" + category + "/" + testname);

        try {
            createProject(category, description.getMethodName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createProject(String category, String test) throws CoreException {
        workspace = ResourcesPlugin.getWorkspace();

        IWorkspaceRoot root = getWorkspace().getRoot();
        IProject project = root.getProject(category + "_" + test);

        if (project.exists()) {
            project.delete(true, true, null);
        }
        project.create(null);
        project.open(null);

        IFolder targetFolder = createFolder(project, "target");
        IFolder srcFolder = createFolder(project, "src");

        IProjectDescription description = project.getDescription();
        description.setNatureIds(new String[]{JavaCore.NATURE_ID});
        project.setDescription(description, null);

        javaProject = JavaCore.create(project);
        javaProject.setOutputLocation(targetFolder.getFullPath(), null);
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, javaProject.getOptions(false));

        IClasspathEntry classpathEntry = JavaCore.newSourceEntry(srcFolder.getFullPath());
        IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(new Path(new File("dist/lombok.jar").getAbsolutePath()), null, null);
        IClasspathEntry javaRtEntry = JavaCore.newLibraryEntry(new Path(new File("lib/rtstubs18.jar").getAbsolutePath()), null, null);
        javaProject.setRawClasspath(new IClasspathEntry[]{classpathEntry, newLibraryEntry, javaRtEntry}, null);

        packageFragment = javaProject.getPackageFragmentRoot(srcFolder).createPackageFragment("pkg", false, null);
    }

    protected IFolder createFolder(IProject project, String name) throws CoreException {
        IFolder folder = project.getFolder(name);
        if (folder.exists()) {
            folder.delete(true, null);
        }
        folder.create(false, true, null);
        return folder;
    }

    protected ICompilationUnit createCompilationUnit(File file, IPackageFragment pkg) throws JavaModelException, FileNotFoundException {
        return pkg.createCompilationUnit(file.getName(), getContent(file), true, null);
    }

    protected String getContent(File f) throws FileNotFoundException {
        Scanner scanner = new Scanner(f);
        String text = scanner.useDelimiter("\\A").next();
        scanner.close();
        return text;
    }
}