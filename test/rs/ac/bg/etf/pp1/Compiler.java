package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java_cup.runtime.*;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class Compiler {

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void main(String[] args) throws Exception {
		
		Reader bufferReader = null;
		Logger log = Logger.getLogger(Compiler.class);
	
		try {
			File sourceCode;
			if (args.length >= 1) {
			    sourceCode = new File(args[0]);
			} 
			else {
			    sourceCode = new File("test/program.mj");
			}

			log.info("Source file: " + sourceCode.getAbsolutePath());
			
			bufferReader = new BufferedReader(new FileReader(sourceCode));
			Yylex lexer = new Yylex(bufferReader);
			
			/* FORMIRANJE AST STABLA */
			
			MJParser parser = new MJParser(lexer);
	        Symbol s = parser.parse();  
	        Program program = (Program)(s.value); 
	        
	        /* ISPIS AST STABLA */
	        
			log.info(program.toString(""));	
			
			/* INICIJALIZACIJA TABELE SIMBOLA */
			
			Tab.init(); 
			Struct boolType = new Struct(Struct.Bool);
			Obj boolObj = Tab.insert(Obj.Type, "bool", boolType);
			boolObj.setAdr(-1);
			boolObj.setLevel(-1);
			
			/* SEMANTICKA ANALIZA */
			
			log.info("============== SEMANTICKA ANALIZA ===================");
			SemanticAnalyzer semantic= new SemanticAnalyzer();
			
			program.traverseBottomUp(semantic);
			
			log.info("==================SINTAKSNA ANALIZA====================");
			CounterVisitor counter = new CounterVisitor();
			program.traverseBottomUp(counter);
			System.out.println(counter.enumCount + " enums");
			System.out.println(counter.methodCount + " methods in the program");
			System.out.println(counter.globalVarCount + " global variables");
			System.out.println(counter.globalConstCount + " global constants");
			System.out.println(counter.globalArrayCount + " global arrays");
			System.out.println(counter.localVarMainCount + " local variables in main");
			System.out.println(counter.statementMainCount + " statements in main");
			System.out.println(counter.functionCallMainCount + " function calls in main");			
			
			Tab.dump();

						
			if(!parser.errorDetected && semantic.passed()){
				
				/* GENERISANJE KODA */
				
				String objName = sourceCode.getPath().replaceAll("\\.mj$", ".obj");
				File objFile = new File(objName);
				if(objFile.exists()) objFile.delete();
				
				CodeGenerator codeGenerator = new CodeGenerator();
				program.traverseBottomUp(codeGenerator);
				Code.dataSize = semantic.nVars;
				Code.mainPc = codeGenerator.getMainPc();
				Code.write(new FileOutputStream(objFile));
				
				log.info("Generisanje uspesno zavrseno!");
			}else{
				log.error("Generisanje NIJE uspesno zavrseno!");
			}
			
		} 
		
		finally {
			if (bufferReader != null) try { bufferReader.close(); } catch (IOException e1) { log.error(e1.getMessage(), e1); }
		}

	}
	
	
}
