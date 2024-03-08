package at.kaindorf.ormapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Project: Exa_OR-Mapper_4CHIF
 * Created by: SF
 * Date: 01.03.2024
 * Time: 09:03
 */
@Target(ElementType.TYPE)  // define annotation for class-level
@Retention(RetentionPolicy.RUNTIME)  // annotation is available at runtime for reflections
public @interface Entity {
  String name() default "";
}
