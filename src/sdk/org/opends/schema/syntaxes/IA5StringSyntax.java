package org.opends.schema.syntaxes;

import static org.opends.server.schema.SchemaConstants.SYNTAX_IA5_STRING_OID;
import static org.opends.server.schema.SchemaConstants.SYNTAX_IA5_STRING_NAME;
import static org.opends.server.schema.SchemaConstants.SYNTAX_IA5_STRING_DESCRIPTION;
import org.opends.server.types.ByteSequence;
import org.opends.messages.MessageBuilder;
import org.opends.messages.Message;
import static org.opends.messages.SchemaMessages.WARN_ATTR_SYNTAX_IA5_ILLEGAL_CHARACTER;
import org.opends.schema.SchemaUtils;

/**
 * This class implements the IA5 string attribute syntax, which is simply a
 * set of ASCII characters.  By default, they will be treated in a
 * case-insensitive manner, and equality, ordering, substring, and approximate
 * matching will be allowed.
 */
public class IA5StringSyntax extends SyntaxImplementation
{
  public IA5StringSyntax()
  {
    super(SYNTAX_IA5_STRING_OID, SYNTAX_IA5_STRING_NAME,
        SYNTAX_IA5_STRING_DESCRIPTION, SchemaUtils.RFC4512_ORIGIN);
  }

  public boolean isHumanReadable() {
    return true;
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
    // We will allow any value that does not contain any non-ASCII characters.
    // Empty values are acceptable as well.
    byte b;
    for (int i = 0; i < value.length(); i++)
    {
      b = value.byteAt(i);
      if ((b & 0x7F) != b)
      {

        Message message = WARN_ATTR_SYNTAX_IA5_ILLEGAL_CHARACTER.get(
                value.toString(), String.valueOf(b));
        invalidReason.append(message);
        return false;
      }
    }

    return true;
  }
}
