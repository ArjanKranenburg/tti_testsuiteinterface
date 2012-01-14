package org.testtoolinterfaces.testsuiteinterface;

import java.util.Hashtable;

import org.testtoolinterfaces.testsuite.TestLink;
import org.testtoolinterfaces.testsuite.TestLinkImpl;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

/**
 * @author Arjan Kranenburg 
 * 
 * <testgrouplink id="..." type="..." sequence="..." [any]="...">
 *   ...
 * </testgrouplink>
 *
 * <testcaselink id="..." type="..." sequence="..." [any]="...">
 *   ...
 * </testcaselink>
 * 
 */
public abstract class TestLinkXmlHandler extends XmlHandler
{
	public static final String ELEMENT_ID = "id";
	public static final String ELEMENT_TYPE = "type";
	public static final String ELEMENT_SEQ = "sequence";
	
	private String myLink;

	private String myId;
	private String myType;
	private int mySequence;
	private Hashtable<String, String> myAnyAttributes;

	public TestLinkXmlHandler( XMLReader anXmlReader, String aStartElement )
	{
		super(anXmlReader, aStartElement);
		Trace.println(Trace.CONSTRUCTOR);
		
		this.reset();
	}

	@Override
	public void handleStartElement(String aQualifiedName)
	{
   		//nop;
    }

	@Override
	public void handleCharacters(String aValue)
	{
		Trace.print(Trace.SUITE, "handleCharacters( " 
		            + aValue, true );
		String link = aValue.trim();
		if ( ! link.isEmpty() )
		{
			myLink += link;
		}
    }
    
	@Override
	public void handleEndElement(String aQualifiedName)
	{
		//nop
    }

    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
     	if (aQualifiedName.equalsIgnoreCase(super.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.append( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ELEMENT_ID))
		    	{
		        	myId = att.getValue(i);
		    	}
		    	else if (att.getQName(i).equalsIgnoreCase(ELEMENT_TYPE))
		    	{
		        	myType = att.getValue(i);
		    	}
		    	else if (att.getQName(i).equalsIgnoreCase(ELEMENT_SEQ))
		    	{
		        	mySequence = Integer.valueOf( att.getValue(i) ).intValue();
		    	}
		    	else
		    	{
		    		myAnyAttributes.put(att.getQName(i), att.getValue(i));
		    	}
		    }
    	}
		Trace.append( Trace.SUITE, " )\n" );
    }

	@Override
	/** 
	 * @param aQualifiedName the name of the childElement
	 * 
	 */
	public void handleGoToChildElement(String aQualifiedName)
	{
		// nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		// nop
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);

		myLink = "";
		
		myId = "";
		myType = "";
		mySequence = 0;
		myAnyAttributes = new Hashtable<String, String>();
	}

	/**
	 * @return the myLink
	 */
	public TestLink getLink()
	{
		return new TestLinkImpl( myLink, myType );
	}

	/**
	 * @return the myId
	 */
	public String getId()
	{
		return myId;
	}

	/**
	 * @return the mySequence
	 */
	public int getSequence()
	{
		return mySequence;
	}

	/**
	 * @return the myAnyAttributes
	 */
	public Hashtable<String, String> getAnyAttributes()
	{
		return myAnyAttributes;
	}
}
