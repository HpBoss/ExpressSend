package com.noah.internet

import java.lang.RuntimeException

/**
 * @Auther: 何飘
 * @Date: 3/29/21 17:23
 * @Description:
 */
class ApiException(override val message: String?): RuntimeException()