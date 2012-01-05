package com.eightbitmage.moonscript.lang.parser.moon;

import com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes;
import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.parser.MoonPsiBuilder;
import com.eightbitmage.moonscript.lang.parser.oldmoon.*;
import com.intellij.diagnostic.PluginException;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class MoonParser implements PsiParser, MoonElementTypes {

    private static final Logger LOG = Logger.getInstance("#MoonParser");

    private final Project project;


    IElementType t = null;  /* current token */
    IElementType lookahead = null;  /* look ahead token */
    private MoonPsiBuilder builder;

    public MoonParser(Project project) {
        this.project = project;
    }


//    @NotNull
//    public ASTNode parse(IElementType root, PsiBuilder builder) {
//
//        final PsiBuilder.Marker rootMarker = builder.mark();
//
//        while (!builder.eof()) {
//            parse(builder);
//        }
//        rootMarker.done(root);
//
//        return builder.getTreeBuilt();
//
//    }

    void next() {
        //lastline = linenumber;
        builder.advanceLexer();
        t = builder.getTokenType();
    }

    void lookahead() {
        PsiBuilder.Marker current = builder.mark();
        builder.advanceLexer();
        lookahead = builder.getTokenType();
        current.rollbackTo();
        t = builder.getTokenType();
    }

     @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {

        final MoonPsiBuilder psiBuilder = new MoonPsiBuilder(builder);
        try {

            final PsiBuilder.Marker rootMarker = psiBuilder.mark();

            MoonParser lexstate = new MoonParser(project);

            lexstate.builder = psiBuilder;
            lexstate.t = psiBuilder.getTokenType();

            while (!psiBuilder.eof()) {
                parse(psiBuilder);
            }

//            lexstate.chunk();

            //int pos = psiBuilder.getCurrentOffset();
           // PsiBuilder.Marker mark  = psiBuilder.mark();
//            while (!psiBuilder.eof())
//                psiBuilder.advanceLexer();

            //if (psiBuilder.getCurrentOffset()>pos)
            //    mark.error("Unparsed code");
            //else
            //    mark.drop();

            if (root != null) {
                rootMarker.done(root);
            }

        } catch (ProcessCanceledException e) {
            throw e;
        } catch (Exception e) {
            throw new PluginException("Exception During Parse At Offset: "+ builder.getCurrentOffset() + "\n\n" + builder.getOriginalText(), e, PluginId.getId("Moon"));
        }

        return builder.getTreeBuilt();
    }

    boolean block_follow(IElementType token) {
        return token == ELSE || token == ELSEIF ||
                token == END || token == UNTIL || token == null;
    }

    private void chunk() {
        boolean islast = false;
        //this.enterlevel();
        while (!islast && !block_follow(this.t)) {
            islast = this.statement();
            //this.testnext(SEMI);
        }
        //this.leavelevel();
    }

//    static final int DEC_REF = 0;
//    static final int DEC_G = 2;
//    static final int DEC_GL = 3;
//    void singlevar(ExpDesc var, int statementType) {
//        PsiBuilder.Marker ref = builder.mark();
//
//        PsiBuilder.Marker mark = builder.mark();
//        String varname = this.str_checkname();
//
//        FuncState fs = this.fs;
//        int type = fs.singlevaraux(varname, var, 1);
//        switch (type) {
//
//            case VGLOBAL:
//                var.info = fs.stringK(varname); /* info points to global name */
//                mark.done(statementType >= DEC_G ? GLOBAL_NAME_DECL : GLOBAL_NAME);
//                break;
//
//            case VUPVAL:
//                mark.done(UPVAL_NAME);
//                break;
//
//            case VLOCAL:
//                mark.done(statementType == DEC_GL ? LOCAL_NAME_DECL : LOCAL_NAME);
//                break;
//
//
//            default:
//                mark.error("Impossible identifier type");
//        }
//
//        ref.done(REFERENCE);
//    }

    void primaryexp(ExpDesc v, int statementType) {
        /*
           * primaryexp -> prefixexp { `.' NAME | `[' exp `]' | `:' NAME funcargs |
           * funcargs }
           */

        PsiBuilder.Marker mark = builder.mark();

        //FuncState fs = this.fs;
        //this.prefixexp(v, statementType);
        for (; ;) {
            if (this.t == DOT) { /* field */
                //this.field(v);

                mark.done(GETTABLE);
                mark = mark.precede();
                mark.done(COMPOUND_REFERENCE);
                mark = mark.precede();

            } else if (this.t == LBRACK) { /* `[' exp1 `]' */
                //ExpDesc key = new ExpDesc();

                //fs.exp2anyreg(v);
                //this.yindex(key);
                //fs.indexed(v, key);

                mark.done(GETTABLE);
                mark = mark.precede();
                mark.done(COMPOUND_REFERENCE);
                mark = mark.precede();

            } else if (this.t == COLON) { /* `:' NAME funcargs */
                //ExpDesc key = new ExpDesc();

                this.next();

                PsiBuilder.Marker tmp = builder.mark();
                //this.checkname(key);
                tmp.done(FIELD_NAME);

                mark.done(GETTABLE);
                mark = mark.precede();
                mark.done(COMPOUND_REFERENCE);
                mark = mark.precede();

                //fs.self(v, key);


                //this.funcargs(v);
                mark.done(FUNCTION_CALL_EXPR);

                mark = mark.precede();
            } else if (this.t == LPAREN
                    || this.t == STRING || this.t == LONGSTRING
                    || this.t == LCURLY) { /* funcargs */
                //fs.exp2nextreg(v);

                //this.funcargs(v);
                mark.done(FUNCTION_CALL_EXPR);
                mark = mark.precede();

                //break;
            } else {
                mark.drop();
                return;
            }
        }
    }


    private static final short PRI_CALL = 0x0001;
    private static final short PRI_COMP = 0x0002;
    static final int NO_JUMP = (-1);

    short primaryexp_org(ExpDesc v) {
        boolean isfunc = false;
        boolean isCompound = false;
        /*
           * primaryexp -> prefixexp { `.' NAME | `[' exp `]' | `:' NAME funcargs |
           * funcargs }
           */
        //FuncState fs = this.fs;
        //this.prefixexp(v, DEC_REF);
        for (;;) {
            if (this.t == DOT) { /* field */
                //this.field_org(v);
                isfunc = false;
                isCompound = true;
            } else if (this.t == LBRACK) { /* `[' exp1 `]' */
                com.eightbitmage.moonscript.lang.parser.oldmoon.ExpDesc key = new com.eightbitmage.moonscript.lang.parser.oldmoon.ExpDesc();
                //this.yindex_org(key);
                //fs.indexed(v, key);
                isfunc = false;
                isCompound = true;
            } else if (this.t == COLON) { /* `:' NAME funcargs */
                com.eightbitmage.moonscript.lang.parser.oldmoon.ExpDesc key = new com.eightbitmage.moonscript.lang.parser.oldmoon.ExpDesc();
                this.next();
                //this.checkname(key);
                //fs.self(v, key);
                //this.funcargs_org(v);
                isfunc = true;
                isCompound = true;
            } else if (this.t == LPAREN
                    || this.t == STRING || this.t == LONGSTRING
                    || this.t == LCURLY) { /* funcargs */
                //this.funcargs_org(v);
                isfunc = true;
            } else {
                short rc = isfunc ? PRI_CALL : 0;
                rc |= isCompound ? PRI_COMP : 0;

                return rc;
            }

        }
    }

    void exprstat() {
        /* stat -> func | assignment */
        //FuncState fs = this.fs;

        /* DANGER - because of this, this parser will produce invalid bytecode */
        /* this is here so we can know which rule we are actually processing */
        /* because unlike the lua parser, we need to know in advance */
        LHS_assign v = new LHS_assign();
        PsiBuilder.Marker lookahead = builder.mark();


        short info = primaryexp_org(v.v);
        boolean isassign = (info & PRI_CALL) == 0;
        boolean isCompound = (info & PRI_COMP) != 0;
        boolean isComplete = !isassign;
        if (isassign)  {// need to see if it is a complete assignment statement
            while(t != ASSIGN && !builder.eof() && !MoonTokenTypes.KEYWORDS.contains(t))
                next();

            if (t == ASSIGN)
                isComplete = true;
        }
        lookahead.rollbackTo();
        this.t = builder.getTokenType();

        v = new LHS_assign();

        PsiBuilder.Marker outer = builder.mark();

        //this.primaryexp(v.v, (isassign&&!isCompound&&isComplete)?DEC_G:DEC_REF);

//        if (v.v.k == VCALL) /* stat -> func */ {
//            if (isassign)
//                builder.error("invalid assign prediction (is call)");
//
//
//            outer.done(FUNCTION_CALL);
//            FuncState.SETARG_C(fs.getcodePtr(v.v), 1); /* call statement uses no results */
//        }
//        else { /* stat -> assignment */
//            if (!isassign)
//                builder.error("invalid call prediction (is assignment)");
//
//            v.prev = null;
//            this.assignment(v, 1, outer);
//
//            outer.precede().done(ASSIGN_STMT);
//        }
    }

    boolean statement() {

        try {

//           if (t == NAME) {
//                parseIdentifier(builder);
//                return false;
//           }

            //log.info(">>> statement");
//            int line = this.linenumber; /* may be needed for error messages */
//
//            if (this.t == IF) { /* stat -> ifstat */
//                this.ifstat(line);
//                return false;
//            }
//            if (this.t == WHILE) { /* stat -> whilestat */
//                this.whilestat(line);
//                return false;
//            }
//            if (this.t == DO) { /* stat -> DO block END */
//                PsiBuilder.Marker mark = builder.mark();
//                this.next(); /* skip DO */
//                this.block();
//                this.check_match(END, DO, line);
//                mark.done(DO_BLOCK);
//                return false;
//            }
//            if (this.t == FOR) { /* stat -> forstat */
//                this.forstat(line);
//                return false;
//            }
//            if (this.t == REPEAT) { /* stat -> repeatstat */
//                this.repeatstat(line);
//                return false;
//            }
//            if (this.t == FUNCTION) {
//                this.funcstat(line); /* stat -> funcstat */
//                return false;
//            }
//            if (this.t == LOCAL) { /* stat -> localstat */
//                PsiBuilder.Marker stat = builder.mark();
//                this.next(); /* skip LOCAL */
//                if (this.t == FUNCTION) /* local function? */
//                    this.localfunc(stat);
//                else
//                    this.localstat(stat);
//                return false;
//            }
//            if (this.t == RETURN) { /* stat -> retstat */
//                this.retstat();
//                return true; /* must be last statement */
//            }
//            if (this.t == BREAK) { /* stat -> breakstat */
//                this.next(); /* skip BREAK */
//                this.breakstat();
//                return true; /* must be last statement */
//            }
//
            this.exprstat();

            return false; /* to avoid warnings */

        } finally {
            //log.info("<<< statement");
        }
    }

    private void parse(MoonPsiBuilder builder) {

        t = builder.getTokenType();

        System.out.println("t->" + t + "[" + builder.getCurrentOffset() + "] " + builder.text() ) ;

        if (t == NAME) {
            parseIdentifier(builder);
        }

//        if (first == JFlexElementTypes.MACROS) {
//            parseMacroDefinition(builder);
//        } else if (first == JFlexElementTypes.CLASS_KEYWORD) {
//            parseClassStatement(builder);
//        } else if (first == JFlexElementTypes.STATE_KEYWORD) {
//            parseStateStatement(builder);
//        } else if (first == JFlexElementTypes.XSTATE_KEYWORD) {
//            parseXstateStatement(builder);
//        } else if (first == JFlexElementTypes.STATE_REF) {
//            parseStateReference(builder);
//        } else if (first == JFlexElementTypes.IMPLEMENTS_KEYWORD) {
//            parseImplementsStatement(builder);
//        } else if (first == JFlexElementTypes.TYPE_KEYWORD) {
//            parseTypeStatement(builder);
//        } else if (first == JFlexElementTypes.JAVA_CODE) {
//            parseJavaCode(builder);
//        } else if (first == JFlexElementTypes.REGEXP_MACROS_REF) {
//            parseMacroReference(builder);
//        } else {
            builder.advanceLexer();
//        }
    }

    private void parseIdentifier(MoonPsiBuilder builder) {

        //PsiBuilder.Marker marker = builder.mark();
        //builder.advanceLexer();
        //marker.done(MoonElementTypes.LOCAL_NAME);



        //IElementType nt = builder.  getTokenType();

    }


//    private void parseStateReference(PsiBuilder builder) {
//        PsiBuilder.Marker mark = builder.mark();
//        builder.advanceLexer();
//        mark.done(JFlexElementTypes.STATE_REF);
//    }
//
//    private void parseCommaSeparatedOptionStatement(PsiBuilder builder, IElementType finishWith) {
//        parseCommaSeparatedOptionStatement(builder, finishWith, JFlexElementTypes.OPTION_PARAMETER);
//    }
//
//    private void parseCommaSeparatedOptionStatement(PsiBuilder builder, IElementType finishWith, IElementType markWith) {
//
//        PsiBuilder.Marker stateMarker = builder.mark();
//        builder.advanceLexer();
//
//        boolean first = true;
//
//        while (builder.getTokenType() == JFlexElementTypes.OPTION_PARAMETER || builder.getTokenType() == JFlexElementTypes.OPTION_COMMA) {
//
//            if (first) {
//                first = false;
//            } else {
//                //parsing commas or go to next expr
//                if (builder.getTokenType() == JFlexElementTypes.OPTION_COMMA) {
//                    builder.advanceLexer();
//                } else {
//                    builder.error(JFlexBundle.message("parser.comma.expected"));
//                }
//            }
//
//            PsiBuilder.Marker interfaceMarker = builder.mark();
//            if (builder.getTokenType() == JFlexElementTypes.OPTION_PARAMETER) {
//                builder.advanceLexer();
//                interfaceMarker.done(markWith);
//            } else {
//                builder.error(JFlexBundle.message("parser.expression.expected"));
//                interfaceMarker.drop();
//                break;
//            }
//        }
//
//        stateMarker.done(finishWith);
//
//    }
//
//    private void parseStateStatement(PsiBuilder builder) {
//        parseCommaSeparatedOptionStatement(builder, JFlexElementTypes.STATE_STATEMENT, JFlexElementTypes.STATE_DEFINITION);
//
//    }
//
//    private void parseXstateStatement(PsiBuilder builder) {
//        parseCommaSeparatedOptionStatement(builder, JFlexElementTypes.STATE_STATEMENT, JFlexElementTypes.STATE_DEFINITION);
//    }
//
//    private void parseMacroDefinition(PsiBuilder builder) {
//
//        PsiBuilder.Marker macroDefinition = builder.mark();
//        builder.advanceLexer();
//
//        if (builder.getTokenType() != JFlexElementTypes.EQ) {
//            builder.error(JFlexBundle.message("parser.eq.expected"));
//        } else {
//            builder.advanceLexer();
//        }
//
//        int found = 0;
//        PsiBuilder.Marker macrovalue = builder.mark();
//
//        while (JFlexElementTypes.REGEXP_SCOPE.contains(builder.getTokenType())) {
//            found++;
//            builder.advanceLexer();
//        }
//
//        if (found == 0) {
//            macrovalue.drop();
//            builder.error(JFlexBundle.message("parser.macrovalue.expected"));
//        } else {
//            macrovalue.done(JFlexElementTypes.REGEXP);
//        }
//
//        macroDefinition.done(JFlexElementTypes.MACRO_DEFINITION);
//
//    }
//
//    private void parseMacroReference(PsiBuilder builder) {
//        PsiBuilder.Marker macroMarker = builder.mark();
//        builder.advanceLexer();
//        macroMarker.done(JFlexElementTypes.REGEXP_MACROS_REF);
//    }
//
//    private void parseImplementsStatement(PsiBuilder builder) {
//        parseCommaSeparatedOptionStatement(builder, JFlexElementTypes.IMPLEMENTS_STATEMENT);
//    }
//
//    private void parseTypeStatement(PsiBuilder builder) {
//        LOG.assertTrue(builder.getTokenType() == JFlexElementTypes.TYPE_KEYWORD);
//        PsiBuilder.Marker marker = builder.mark();
//        builder.advanceLexer();
//        parseOptionParamExpression(builder);
//        marker.done(JFlexElementTypes.TYPE_STATEMENT);
//    }
//
//    private void parseJavaCode(PsiBuilder builder) {
//        PsiBuilder.Marker marker = builder.mark();
//        builder.advanceLexer();
//        marker.done(JFlexElementTypes.JAVA_CODE);
//    }
//
//    private void parseClassStatement(PsiBuilder builder) {
//        LOG.assertTrue(builder.getTokenType() == JFlexElementTypes.CLASS_KEYWORD);
//        PsiBuilder.Marker marker = builder.mark();
//        builder.advanceLexer();
//        parseOptionParamExpression(builder);
//        marker.done(JFlexElementTypes.CLASS_STATEMENT);
//    }
//
//    private void parseOptionParamExpression(PsiBuilder builder) {
//        PsiBuilder.Marker expr = builder.mark();
//        if (builder.getTokenType() != JFlexElementTypes.OPTION_PARAMETER) {
//            builder.error(JFlexBundle.message("parser.expression.expected"));
//            expr.drop();
//        } else {
//            builder.advanceLexer();
//            expr.done(JFlexElementTypes.OPTION_PARAMETER);
//        }
//    }
}
