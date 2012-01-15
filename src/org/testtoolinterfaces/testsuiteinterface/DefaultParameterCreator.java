/**
 * 
 */
package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.ParameterImpl;
import org.testtoolinterfaces.testsuite.TestSuiteException;

/**
 * Abstract Utility class for creating parameters from a type and string representation of a value.
 * 
 * @author Arjan Kranenburg
 * @see http://www.testtoolinterfaces.org
 *
 */
public abstract class DefaultParameterCreator
{
	/**
	 * Creates a parameter with a value based on the type and a string representation
	 * of the value. E.g. type="int" and value="3" will create a parameter with value Integer("3")
	 * 
	 * supported types by this creator:
	 * - string
	 * - int
	 * 
	 * @param aName - the Parameter name
	 * @param aType - the type of the value (case insensitive)
	 * @param aValue - A string representation of the value
	 * @return the parameter
	 * 
	 * @throws TestSuiteException when the type is not supported
	 */
	public static ParameterImpl createParameter( String aName,
	                                             String aType,
	                                             String aValue ) throws TestSuiteException
	{
		if ( aType.equalsIgnoreCase( "string" ) )
		{
			return new ParameterImpl(aName, (String) aValue);
		}			

		if ( aType.equalsIgnoreCase( "int" ) )
		{
			return new ParameterImpl(aName, new Integer(aValue) );
		}

		throw new TestSuiteException("Parameter type " + aType + " is not supported for this interface", aName);
	}
}
