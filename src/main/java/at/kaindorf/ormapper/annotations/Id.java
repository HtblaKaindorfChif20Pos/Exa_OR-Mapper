package at.kaindorf.ormapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Project: Exa_OR-Mapper_4CHIF
 * Created by: SF
 * Date: 06.03.2024
 * Time: 09:14
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
}
