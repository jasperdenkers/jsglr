package org.spoofax.jsglr2.tests.grammars;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.spoofax.jsglr.client.InvalidParseTableException;
import org.spoofax.jsglr2.parsetable.ParseTableReadException;
import org.spoofax.jsglr2.tests.util.BaseTestWithJSGLR1;
import org.spoofax.jsglr2.util.WithGrammar;
import org.spoofax.terms.ParseError;

public class MatchTest extends BaseTestWithJSGLR1 implements WithGrammar {

    public MatchTest() throws ParseError, ParseTableReadException, IOException, InvalidParseTableException,
        InterruptedException, URISyntaxException {
        setupParseTableFromDefFile("match");
    }

    @Test
    public void singleRowSingleColumn() throws ParseError, ParseTableReadException, IOException {
        testSuccessByJSGLR1(
        //@formatter:off
            "match 1 with \n" + 
            "  a -> match 2 with\n" + 
            "    x -> 3\n" + 
            "    y -> 4\n" + 
            "  b -> 5"
        //@formatter:on
        );
    }

}