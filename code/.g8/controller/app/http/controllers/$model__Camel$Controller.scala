package http.controllers

import http.api.ApiController
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.libs.json.JsValue
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import data.models.$model;format="Camel"$
// NOTE: Add the following to conf/routes to enable compilation of this class:
/*
GET       /$model;format="lower,hyphen"$                                  http.controllers.$model;format="Camel"$Controller.index
GET       /$model;format="lower,hyphen"$/:$model;format="camel"$Id        http.controllers.$model;format="Camel"$Controller.show($model;format="camel"$Id:Int)
POST      /$model;format="lower,hyphen"$                                  http.controllers.$model;format="Camel"$Controller.store
PUT       /$model;format="lower,hyphen"$/:$model;format="camel"$Id        http.controllers.$model;format="Camel"$Controller.update($model;format="camel"$Id:Int)
DELETE    /$model;format="lower,hyphen"$/:$model;format="camel"$Id        http.controllers.$model;format="Camel"$Controller.delete($model;format="camel"$Id:Int)
*/

/**
  *
  * $model;format="Camel"$ api controller for Play Scala
  */
class $model;format = "Camel" $Controller@Inject () (cc: ControllerComponents) extends AbstractController (cc) with ApiController with I18nSupport {

  /**
    * Get a Collection of $model;format="Camel"$
    *
    * @return
    */
  def index: Action[Unit] = ApiAction { implicit request =>
  ok ("Paging OK")
}

  /**
    * Save $model;format="Camel"$
    *
    * @return
    */
  def store: Action[JsValue] = ApiActionWithBody {implicit request =>
  readFromRequest[$model;format = "Camel" $] {form =>
  // handle form
  created ()
}
}

  /**
    * Get $model;format="Camel"$ By id
    *
    * @return
    */
  def show ($model;
  format = "camel" $Id: Int): Action[Unit] = ApiAction {implicit request =>

  ok ("OK")
}

  /**
    * Update $model;format="Camel"$ By Id
    *
    * @return
    */
  def update ($model;format = "camel" $Id: Int): Action[JsValue] = ApiActionWithBody {implicit request =>
  readFromRequest[$model;format = "Camel" $] { form =>
  // handle form
  maybeItem (Some ("OK") )
}
}


  /**
    * Delete $model;format="Camel"$ By Id
    *
    * @return
    */
  def delete ($model;format = "camel" $Id: Int): Action[JsValue] = ApiActionWithBody { implicit request =>

  maybeItem (Some ("OK") )
}

}