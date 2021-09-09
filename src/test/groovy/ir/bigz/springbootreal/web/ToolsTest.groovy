package ir.bigz.springbootreal.web

import ir.bigz.springbootreal.commons.util.Utils
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Title

import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@ContextConfiguration(classes = [Utils.class])
@Title("test tools")
class ToolsTest extends Specification{

    //this method just for show BDD test with spock
    def "create date base on zone"(){

        when: "call utils return current dateTime base on specific format"
        def now = Utils.getTimeNow()

        then: "expected printed time is current time"
        ZonedDateTime.now(ZoneId.of("Asia/Tehran")).format(Utils.FORMATTER) ==  now
    }
}
