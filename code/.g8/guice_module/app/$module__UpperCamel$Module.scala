import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

// NOTE: Add the following to conf/routes to enable compilation of this class:
/*
 play.modules.enabled += "$module;format="UpperCamel"$Module"
*/


/*
* $module;format="UpperCamel"$ Guice Inject Module for Play Scala
*/
class $module;format="UpperCamel"$Module extends AbstractModule with ScalaModule {
    override def configure (): Unit = {
      // Bind your Contracts to Implementation here
      // bind(classOf[DummyContract]).to(classOf[DummyInstance])

      // Dependencies for with ScalaModule
      // "net.codingwell" %% "scala-guice" % "4.1.1"
    }
}