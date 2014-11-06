import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;

import me.fm.interceptor.BaseInterceptor;

import org.unique.ioc.annotation.Component;


public class Main {

	public static void main(String[] args) throws UnsupportedEncodingException {
		Annotation[] annotations = BaseInterceptor.class.getAnnotations();
		for (Annotation annotation : annotations) {
            System.out.println(annotation instanceof Component);
        }
	}

}
