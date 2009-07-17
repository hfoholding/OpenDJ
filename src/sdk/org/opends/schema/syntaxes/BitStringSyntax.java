package org.opends.schema.syntaxes;

import org.opends.server.types.ByteSequence;
import static org.opends.server.schema.SchemaConstants.*;
import org.opends.messages.MessageBuilder;
import static org.opends.messages.SchemaMessages.WARN_ATTR_SYNTAX_BIT_STRING_TOO_SHORT;
import static org.opends.messages.SchemaMessages.WARN_ATTR_SYNTAX_BIT_STRING_NOT_QUOTED;
import static org.opends.messages.SchemaMessages.WARN_ATTR_SYNTAX_BIT_STRING_INVALID_BIT;
import org.opends.schema.SchemaUtils;

/**
 * This class defines the bit string attribute syntax, which is comprised of
 * a string of binary digits surrounded by single quotes and followed by a
 * capital letter "B" (e.g., '101001'B).
 */
public class BitStringSyntax extends SyntaxImplementation
{
  /**
   * Creates a new instance of this syntax.
   */
  public BitStringSyntax()
  {
    super(SYNTAX_BIT_STRING_OID, SYNTAX_BIT_STRING_NAME,
        SYNTAX_BIT_STRING_DESCRIPTION, SchemaUtils.RFC4512_ORIGIN);
  }


  /**
   * Indicates whether the provided value is acceptable for use in an attribute
   * with this syntax.  If it is not, then the reason may be appended to the
   * provided buffer.
   *
   * @param  value          The value for which to make the determination.
   * @param  invalidReason  The buffer to which the invalid reason should be
   *                        appended.
   *
   * @return  <CODE>true</CODE> if the provided value is acceptable for use with
   *          this syntax, or <CODE>false</CODE> if not.
   */
  public boolean valueIsAcceptable(ByteSequence value,
                                   MessageBuilder invalidReason)
  {
    String valueString = value.toString().toUpperCase();

    int length = valueString.length();
    if (length < 3)
    {
      invalidReason.append(
              WARN_ATTR_SYNTAX_BIT_STRING_TOO_SHORT.get(value.toString()));
      return false;
    }


    if ((valueString.charAt(0) != '\'') ||
        (valueString.charAt(length-2) != '\'') ||
        (valueString.charAt(length-1) != 'B'))
    {
      invalidReason.append(
              WARN_ATTR_SYNTAX_BIT_STRING_NOT_QUOTED.get(value.toString()));
      return false;
    }


    for (int i=1; i < (length-2); i++)
    {
      switch (valueString.charAt(i))
      {
        case '0':
        case '1':
          // These characters are fine.
          break;
        default:
          invalidReason.append(WARN_ATTR_SYNTAX_BIT_STRING_INVALID_BIT.get(
                  value.toString(), String.valueOf(valueString.charAt(i))));
          return false;
      }
    }


    // If we've gotten here, then everything is fine.
    return true;
  }

  public boolean isHumanReadable() {
    return true;
  }
}
