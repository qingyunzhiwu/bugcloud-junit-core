package bugcloud.junit.core.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class JavaCodeCompileUtils {
	public static Class<?> compile(String className, String javaCodes) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		StrSrcJavaObject srcObject = new StrSrcJavaObject(className, javaCodes);
		Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(srcObject);
		String flag = "-d";
		String outDir = "";
		try {
			URI fileURI = Thread.currentThread().getContextClassLoader().getResource("").toURI();
			File classPath = new File(fileURI);
			outDir = classPath.getAbsolutePath() + File.separator;// + "bugpush" + File.separator;
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		Iterable<String> options = Arrays.asList(flag, outDir);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, fileObjects);
		boolean result = task.call();
		if (result == true) {
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static class StrSrcJavaObject extends SimpleJavaFileObject {
		private String content;

		StrSrcJavaObject(String name, String content) {
			super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.content = content;
		}

		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return content;
		}
	}
}
