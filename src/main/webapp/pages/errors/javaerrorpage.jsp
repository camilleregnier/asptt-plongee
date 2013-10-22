<%@ page isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>Example - "PageContext" Error Data</title>
</head>
<body>
<pre>
  Request URI:  ${pageContext.errorData.requestURI}
  Servlet Name: ${pageContext.errorData.servletName}
  Status Code:  ${pageContext.errorData.statusCode}
  Message:      ${pageContext.errorData.throwable.message}
  Stack Trace:
  <c:forEach var="st" items="${pageContext.errorData.throwable.stackTrace}">
     ${st}
  </c:forEach>
</pre>
</body>
</html>