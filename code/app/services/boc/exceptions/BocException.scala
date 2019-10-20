package services.boc.exceptions

import services.boc.models.BOCApiError

class BocException(error: BOCApiError) extends Exception(error.description)
