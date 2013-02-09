package org.testtoolinterfaces.testsuiteinterface;

import java.util.Hashtable;

import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepCommand;
import org.testtoolinterfaces.testsuite.TestStepScript;
import org.testtoolinterfaces.testsuite.TestStepSelection;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

/**
 * <teststep sequence=...>
 *  <description>
 *    ...
 *  </description>
 *  <parameter>
 *    ...
 *  </parameter>
 *  <command> ... </command>]
 *  ...
 * </teststep>
 * 
 * <teststep sequence=...>
 *  <description>
 *    ...
 *  </description>
 *  <parameter>
 *    ...
 *  </parameter>
 *  <script> ... </script>
 *  ...
 * </teststep>
 * 
 * <if [not="true"] sequence=...>
 *  <description>
 *    ...
 *  </description>
 *  <parameter>
 *    ...
 *  </parameter>
 *  <command> ... </command>]
 *  <then>...</then>
 *  <else>...</else>
 *  ...
 * </if>
 * 
 * <if [not] sequence=...>
 *  <description>
 *    ...
 *  </description>
 *  <parameter>
 *    ...
 *  </parameter>
 *  <script> ... </script>
 *  <then>...</then>
 *  <else>...</else>
 *  ...
 * </if>
 * 
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class TestStepXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "teststep";
	public static final String IF_ELEMENT = "if";
	private static final String THEN_ELEMENT = "then";
	private static final String ELSE_ELEMENT = "else";

	private static final String ATTRIBUTE_SEQUENCE = "sequence";
	private static final String ATTRIBUTE_NOT = "not";

	private static final String DESCRIPTION_ELEMENT = "description";

    private TestInterfaceList myInterfaces;
	private boolean myCheckStepParams = false;

	// The sub-handlers
    private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private CommandXmlHandler myCommandXmlHandler;
	private ScriptXmlHandler myScriptXmlHandler;
	private ParameterXmlHandler myParameterXmlHandler;
	private TestStepSequenceXmlHandler myThenXmlHandler;
	private TestStepSequenceXmlHandler myElseXmlHandler;

	// Needed to create the TestStep
	private int mySequence;
	private boolean myNot=false;
	private String myDescription;
    private ParameterArrayList myParameters;
	private Hashtable<String, String> myAnyAttributes;
	private Hashtable<String, String> myAnyElements;
	private String myAnyValue;
	
    // In case of a TestStepCommand
	private String myCommand;
	private TestInterface myInterface;

    // In case of a TestStepScript
	private String myScript;
	private String myScriptType;

    // In case of a TestStepSelection
	private TestStepSequence myThenSteps;
	private TestStepSequence myElseSteps;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader			The xmlReader
	 * @param aTag					The start-element for this XML Handler
	 * @param anInterfaceList		A list of interfaces
	 * @param aCheckStepParameter	Flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public TestStepXmlHandler( XMLReader anXmlReader,
	                           String aTag,
	                           TestInterfaceList anInterfaceList,
	                           boolean aCheckStepParameter )
	{
		super(anXmlReader, aTag);
		Trace.println(Trace.CONSTRUCTOR, "TestStepXmlHandler( anXmlreader )", true);

		myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addElementHandler(myDescriptionXmlHandler);

		myParameterXmlHandler = new ParameterXmlHandler(anXmlReader);
		this.addElementHandler(myParameterXmlHandler);

		try
		{
			myCommandXmlHandler = new CommandXmlHandler(anXmlReader, anInterfaceList);
		}
		catch (TestSuiteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.addElementHandler(myCommandXmlHandler);

		myScriptXmlHandler = new ScriptXmlHandler(anXmlReader);
		this.addElementHandler(myScriptXmlHandler);

		if (aTag.equalsIgnoreCase(IF_ELEMENT)) {
    		myThenXmlHandler = new TestStepSequenceXmlHandler(this.getXmlReader(), THEN_ELEMENT, anInterfaceList, myCheckStepParams);
    		this.addElementHandler(myThenXmlHandler);

    		myElseXmlHandler = new TestStepSequenceXmlHandler(this.getXmlReader(), ELSE_ELEMENT, anInterfaceList, myCheckStepParams);
    		this.addElementHandler(myElseXmlHandler);
 		}

		myInterfaces = anInterfaceList;
		myCheckStepParams = aCheckStepParameter;

		reset();

	}

	/**
	 * Creates the XML Handler for element teststep
	 * 
	 * @param anXmlReader the xmlReader
	 * @param anInterfaceList a list of interfaces
	 * @param aCheckStepParameter flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public TestStepXmlHandler( XMLReader anXmlReader,
	                           TestInterfaceList anInterfaceList,
	                           boolean aCheckStepParameter )
	{
		this( anXmlReader, START_ELEMENT, anInterfaceList, aCheckStepParameter);
	}

	@Override
    public void processElementAttributes(String aQualifiedName, Attributes att) throws TestSuiteException
    {
		Trace.println(Trace.SUITE, "processElementAttributes( "
				+ aQualifiedName + " )", true );
    	if (aQualifiedName.equalsIgnoreCase(this.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))
		    	{
		    		mySequence = Integer.valueOf( att.getValue(i) ).intValue();
		    		Trace.println( Trace.ALL, "        mySequence -> " + mySequence);
    	    	}
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_NOT))
		    	{
		    		myNot = true;
		    		// The value, if any, is ignored
		    		Trace.println( Trace.ALL, "        myNot -> true");
    	    	}
		    	else
		    	{
		    		myAnyAttributes.put(att.getQName(i), att.getValue(i));
		    	}
		    }
    	}
    }

	@Override
	public void handleStartElement(String aQualifiedName)
	{
    	//nop
	}

	@Override
	public void handleCharacters(String aValue)
	{
		myAnyValue += aValue;
	}

	@Override
	public void handleEndElement(String aQualifiedName)
	{
		if ( ! aQualifiedName.equalsIgnoreCase(this.getStartElement()) )
    	{
			// TODO This will overwrite previous occurrences of the same elements. But that is possible in XML.
			myAnyElements.put(aQualifiedName, myAnyValue);
			myAnyValue = "";
    	}
	}
	
	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
		// NOP
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT))
    	{
    		myDescription  = myDescriptionXmlHandler.getValue();
        	myDescriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ParameterXmlHandler.START_ELEMENT))
    	{
			try
			{
	    		Parameter parameter = myParameterXmlHandler.getParameter();
	    		myParameters.add(parameter);
			}
			catch (TestSuiteException e)
			{
				Warning.println("Cannot add Parameter: " + e.getMessage());
				Trace.print(Trace.SUITE, e);
			}
    		
    		myParameterXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(CommandXmlHandler.START_ELEMENT))
    	{
    		if ( myCommand != null )
    		{
				Warning.println( "Command is defined twice. Last one is used." );   			
    		}
    		if ( myScript != null )
    		{
				Warning.println( "Both Command and Script are defined" );   			
    		}
    		myCommand  = myCommandXmlHandler.getCommand();
    		myInterface = myCommandXmlHandler.getInterface();
    		myParameterXmlHandler.setCurrentInterface(myInterface);
    		myCommandXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ScriptXmlHandler.ELEMENT_START))
    	{
    		if ( myScript != null )
    		{
				Warning.println( "Script is defined twice. Last one is used." );   			
    		}
    		if ( myCommand != null )
    		{
				Warning.println( "Both Command and Script are defined" );   			
    		}
    		myScript = myScriptXmlHandler.getScript();
    		myScriptType = myScriptXmlHandler.getType();
    		myScriptXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(THEN_ELEMENT))
    	{
			myThenSteps = myThenXmlHandler.getSteps();
			myThenXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELSE_ELEMENT))
    	{
			myElseSteps = myElseXmlHandler.getSteps();
			myElseXmlHandler.reset();
    	}
		// else nothing (ignored)
	}

	/**
	 * @return the TestStep
	 * @throws TestSuiteException when not all information is defined
	 */
	public TestStep getStep() throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		TestStep testStep = null;
		if ( myCommand != null )
		{
			testStep = createTestStepCommand();
		}
		else if ( myScript != null )
		{
			testStep = createTestStepScript();
		}
		else
		{
			throw new TestSuiteException( "Cannot make a TestStep Command, Script, or Selection from given information" );
		}

		if ( this.getStartElement().equalsIgnoreCase(IF_ELEMENT) )
		{
			testStep = createTestStepSelection( testStep );
		}
		
		return testStep;
	}

	/**
	 * @return the TestStep as TestStepCommand
	 * 
	 * @throws TestSuiteException	When the interface does not support the command
	 * 								or when the parameters are not correct defined for the command.
	 * @throws Error				When the interface is null -> programming error.
	 */
	private TestStepCommand createTestStepCommand() throws Error, TestSuiteException
	{
		if( myInterface == null )
		{
			throw new Error( "Interface cannot be null" );
		}

		if( ! myInterface.hasCommand(myCommand) )
		{
			throw new TestSuiteException( "Command " + myCommand + " not known for interface " + myInterface.getInterfaceName() );
		}

		/*
		 * TODO We should also create them
		 * In XML the parameters could be defined before the command & interface,
		 * and then myParameterXmlHandler.setCurrentInterface() is still set to default.
		 * How to solve?
		 * 
		 */
		if( myCheckStepParams )
		{
			myInterface.verifyParameters(myCommand, myParameters);
		}

		TestStepCommand tsCommand = new TestStepCommand( mySequence,
		                            myDescription,
		                            myCommand,
		                            myInterface,
		                            myParameters );
		
		tsCommand.setAnyAttributes(myAnyAttributes);
		tsCommand.setAnyElements(myAnyElements);
		
		return tsCommand;
	}

	/**
	 * @return the TestStep as TestStepScript
	 * 
	 */
	private TestStepScript createTestStepScript()
	{
		TestStepScript tsScript = new TestStepScript( mySequence,
		                           myDescription,
		                           myScript,
		                           myScriptType,
		                           myParameters );

		tsScript.setAnyAttributes(myAnyAttributes);
		tsScript.setAnyElements(myAnyElements);
		
		return tsScript;
	}

	/**
	 * @param testStep the if-step
	 * @return the TestStep as TestStepSelection
	 * @throws TestSuiteException 
	 */
	private TestStepSelection createTestStepSelection(TestStep ifStep) throws TestSuiteException
	{
		if ( myThenSteps == null )
		{
			throw new TestSuiteException( "No then-step defined for selection" );
		}

		TestStepSelection tsScript = new TestStepSelection( mySequence,
		                              myDescription,
		                              ifStep,
		                              myNot,
		                              myThenSteps,
		                              myElseSteps );

		tsScript.setAnyAttributes(myAnyAttributes);
		tsScript.setAnyElements(myAnyElements);
		                      		
		return tsScript;
	}

	@Override
	public void reset()
	{
		Trace.println(Trace.SUITE);

		mySequence = 0;
		myDescription = "";
		myParameters = new ParameterArrayList();
		myAnyAttributes = new Hashtable<String, String>();
		myAnyElements = new Hashtable<String, String>();
	    myAnyValue = "";
	
		myCommand = null;
		myInterface = myInterfaces.getInterface( CommandXmlHandler.DEFAULT_INTERFACE_NAME );
		myParameterXmlHandler.setCurrentInterface(myInterface);

		myScript = null;
		myScriptType = "";
		
        myThenSteps = new TestStepSequence();
        myElseSteps = new TestStepSequence();
	}
}
