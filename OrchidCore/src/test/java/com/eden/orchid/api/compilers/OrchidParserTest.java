package com.eden.orchid.api.compilers;

import com.caseyjbrooks.clog.Clog;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public final class OrchidParserTest {

    private OrchidParser underTest;

    @BeforeEach
    public void testSetup() {
        Clog.getInstance().setMinPriority(Clog.Priority.FATAL);
        underTest = new OrchidParser(100) {
            @Override public String[] getSourceExtensions() { return new String[] { "" }; }
            @Override public JSONObject parse(String extension, String input) { return new JSONObject(); }
        };
    }

    @Test
    public void getCompilerExtensions() throws Throwable {
        assertThat(underTest.getPriority(), is(equalTo(100)));
        assertThat(underTest.getDelimiter(), is(nullValue()));
        assertThat(underTest.getDelimiterString(), is(nullValue()));
    }
}
