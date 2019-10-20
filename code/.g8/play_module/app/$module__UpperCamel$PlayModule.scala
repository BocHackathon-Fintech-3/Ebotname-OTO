import play.api.inject.Module
import play.api.{Configuration, Environment}

// NOTE: Add the following to conf/routes to enable compilation of this class:
/*
  play.modules.enabled += "$module;format="UpperCamel"$Module"
 */

/*
 * $module;format="UpperCamel"$ Guice Inject Module for Play Scala
 */
class $module;format="UpperCamel"$PlayModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(

    // Bind your Contracts to Implementation here
    // bind(classOf[DummyContract]).to(classOf[DummyInstance])
  )
}