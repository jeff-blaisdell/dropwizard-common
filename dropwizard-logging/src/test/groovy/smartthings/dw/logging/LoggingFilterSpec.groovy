package smartthings.dw.logging

import org.apache.log4j.MDC
import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class LoggingFilterSpec extends Specification {
	LoggingFilter filter = new LoggingFilter()
	HttpServletRequest request = Mock()
	ServletResponse response = null
	FilterChain chain = Mock()

	def setup() {
		MDC.clear()
	}

	def 'null and empty values return a random UUID'() {
		when:
		filter.doFilter(request, response, chain)

		then:
		1 * chain.doFilter(request, response) >> {
			assert LoggingContext.loggingId != null
			return
		}
		1 * request.getHeader(LoggingContext.CORRELATION_ID_HEADER) >> id
		0 * _

		// context is cleared when filter exits
		LoggingContext.loggingId == null

		where:
		id << [null, '']
	}

	def 'value return the header UUID if it exists'() {
		when:
		filter.doFilter(request, response, chain)

		then:
		1 * chain.doFilter(request, response) >> {
			assert LoggingContext.loggingId == id
			return
		}
		1 * request.getHeader(LoggingContext.CORRELATION_ID_HEADER) >> id
		0 * _

		// context is cleared when filter exits
		LoggingContext.loggingId == null


		where:
		id << ['test1', 'hello2']
	}
}
