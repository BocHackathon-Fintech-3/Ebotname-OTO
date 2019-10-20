package services.dialogflow

import play.api.libs.json.{JsArray, JsObject, Json, JsValue}

object AssistantResponse {
  def getListItem(
    key: String,
    name: String,
    description: String,
    url: String = "https://www.bankofcyprus.com/globalassets/boc-logo-small.png"
  ) = Json.obj(
    "info" -> Json.obj("key" -> key),
    "title" -> name,
    "description" -> description,
    "image" -> Json.obj("imageUri" -> url, "accessibilityText" -> description)
  )

  def fulfillmentList(title: String, items: Seq[JsObject]) = Json.obj(
    "fulfillmentText" -> title,
    "fulfillmentMessages" -> sayWithTextAndListItems(title, items)
  )
  def sayWithTextAndListItems(
    title: String,
    items: Seq[JsObject] = Seq(Json.obj())
  ): Seq[JsObject] =
    sayWithText(title, Seq(title)) ++ Seq(getListObject(title, items))

  def getListObject(title: String, items: Seq[JsObject]) =
    Json.obj(
      "platform" -> "ACTIONS_ON_GOOGLE",
      "listSelect" -> Json.obj("title" -> title, "items" -> items)
    )

  def sayWithText(text: String, somethings: Seq[String]): Seq[JsObject] =
    Seq(say(somethings), Json.obj("text" -> Json.obj("text" -> Seq(text))))

  def say(something: Seq[String]): JsObject =
    Json.obj("platform" -> "ACTIONS_ON_GOOGLE", "simpleResponses" -> Json.obj {
      "simpleResponses" -> something.map { v =>
        Json.obj("textToSpeech" -> v)
      }
    })

  def accounts(): JsValue =
    Json.parse(
      """
        |{
        |	"fulfillmentText": "hello",
        |	"fulfillmentMessages": [{
        |		"platform": "ACTIONS_ON_GOOGLE",
        |		"simpleResponses": {
        |			"simpleResponses": [{
        |				"textToSpeech": "hello"
        |			}]
        |		}
        |	}, {
        |		"text": {
        |			"text": ["hello"]
        |		}
        |	}, {
        |		"platform": "ACTIONS_ON_GOOGLE",
        |		"listSelect": {
        |			"title": "here are the accounts:",
        |			"items": [{
        |					"info": {
        |						"key": "1"
        |					},
        |					"title": "GEORGE ANDREOU",
        |					"description": "#sessionvars.george EUR",
        |					"image": {
        |						"imageUri": "https://www.bankofcyprus.com/globalassets/boc-logo-small.png",
        |						"accessibilityText": "#sessionvars.george EUR"
        |					}
        |				}, {
        |					"info": {
        |						"key": "2"
        |					},
        |					"title": "DEMETRIS KOSTA",
        |					"description": "#sessionvars.demetris EUR",
        |					"image": {
        |						"imageUri": "https://www.bankofcyprus.com/globalassets/boc-logo-small.png",
        |						"accessibilityText": "#sessionvars.demetris EUR"
        |					}
        |				}, {
        |					"info": {
        |						"key": "3"
        |					},
        |					"title": "ANDREAS MICHAEL",
        |					"description": "#sessionvars.andreas EUR",
        |					"image": {
        |						"imageUri": "https://www.bankofcyprus.com/globalassets/boc-logo-small.png",
        |						"accessibilityText": "#sessionvars.andreas EUR"
        |					}
        |				},
        |				{
        |					"info": {
        |						"key": "4"
        |					},
        |					"title": "CHRISTOS SAVVA",
        |					"description": "#sessionvars.christos EUR",
        |					"image": {
        |						"imageUri": "https://www.bankofcyprus.com/globalassets/boc-logo-small.png",
        |						"accessibilityText": "#sessionvars.christos EUR"
        |					}
        |				}
        |			]
        |		}
        |	}]
        |}
        |""".stripMargin
    )

  def accountSelect(title: String): JsObject = Json.obj {
    "fulfillmentText" -> title
    "fulfillmentMessages" -> Json.arr(
      Json.obj(
        "platform" -> "ACTIONS_ON_GOOGLE",
        "simpleResponses" -> Json.obj {
          "simpleResponses" -> Json.arr(
            Json.obj("textToSpeech" -> "here are the accounts:")
          )
        },
        "lang" -> "en"
      ),
      Json.obj(
        "text" -> Json.obj("text" -> Json.arr("here are the accounts:")),
        "lang" -> "en"
      ),
      Json.obj(
        "platform" -> "ACTIONS_ON_GOOGLE",
        "listSelect" -> Json
          .obj(
            "title" -> "here are the accounts:",
            "items" -> Json.arr(
              Json.obj(
                "info" -> Json.obj("key" -> "1"),
                "title" -> "GEORGE ANDREOU",
                "image" -> Json.obj(
                  "imageUri" -> "https://www.bankofcyprus.com/globalassets/boc-logo-small.png",
                  "accessibilityText" -> "#sessionvars.george EUR"
                )
              )
            )
          )
      )
    )
  }

}
