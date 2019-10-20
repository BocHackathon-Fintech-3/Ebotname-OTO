package actions

import services.dialogflow.models.DialogFlowConversation

trait ActionHandler {
  def actions: Seq[String] = Seq.empty

  def canHandle(conversation: DialogFlowConversation): Boolean = actions.contains(conversation.action)

  def handle(conversation: DialogFlowConversation)
}
