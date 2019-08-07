package com.watchnext

import com.eshop.BuildInfo
import scribe.Logging

object Boot extends Logging {

  def main(args: Array[String]): Unit = {

    logger.info(s"[E-Shop] started (${BuildInfo.toString})")


  }

}
