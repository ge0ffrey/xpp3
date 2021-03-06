/*
 * $Header: /l/extreme/cvspub/XPP3/java/src/java/xpath/org/xmlpull/v1/builder/xpath/jaxen/function/ext/LowerFunction.java,v 1.2 2005/08/11 22:44:02 aslom Exp $
 * $Revision: 1.2 $
 * $Date: 2005/08/11 22:44:02 $
 *
 * ====================================================================
 *
 * Copyright (C) 2000-2002 bob mcwhirter & James Strachan.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions, and the disclaimer that follows 
 *    these conditions in the documentation and/or other materials 
 *    provided with the distribution.
 *
 * 3. The name "Jaxen" must not be used to endorse or promote products
 *    derived from this software without prior written permission.  For
 *    written permission, please contact license@jaxen.org.
 * 
 * 4. Products derived from this software may not be called "Jaxen", nor
 *    may "Jaxen" appear in their name, without prior written permission
 *    from the Jaxen Project Management (pm@jaxen.org).
 * 
 * In addition, we request (but do not require) that you include in the 
 * end-user documentation provided with the redistribution and/or in the 
 * software itself an acknowledgement equivalent to the following:
 *     "This product includes software developed by the
 *      Jaxen Project (http://www.jaxen.org/)."
 * Alternatively, the acknowledgment may be graphical using the logos 
 * available at http://www.jaxen.org/
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE Jaxen AUTHORS OR THE PROJECT
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the Jaxen Project and was originally 
 * created by bob mcwhirter <bob@werken.com> and 
 * James Strachan <jstrachan@apache.org>.  For more information on the 
 * Jaxen Project, please see <http://www.jaxen.org/>.
 * 
 * $Id: LowerFunction.java,v 1.2 2005/08/11 22:44:02 aslom Exp $
 */

package org.xmlpull.v1.builder.xpath.jaxen.function.ext;

import org.xmlpull.v1.builder.xpath.jaxen.Context;
import org.xmlpull.v1.builder.xpath.jaxen.FunctionCallException;
import org.xmlpull.v1.builder.xpath.jaxen.Navigator;
import org.xmlpull.v1.builder.xpath.jaxen.function.StringFunction;

import java.util.List;
import java.util.Locale;

/**
 * <p><code><i>string</i> lower-case(<i>string</i>)</code>
 *
 * This function can take a second parameter of the 
 * <code>Locale</code> to use for the String conversion.
 * </p>
 *
 * <p>
 * For example
 *
 * <code>lower-case( /foo/bar )</code>
 * <code>lower-case( /foo/@name, $myLocale )</code>
 * </p>
 *
 * @author mark wilson (markw@wilsoncom.de)
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 */
public class LowerFunction extends LocaleFunctionSupport
{

    public Object call(Context context,
                       List args) throws FunctionCallException
    {
        Navigator navigator = context.getNavigator();
        int size = args.size();
        if (size > 0)
        {
            Object text = args.get(0);
            Locale locale = null;
            if (size > 1)
            {  
                locale = getLocale( args.get(1), navigator );
            }
            return evaluate( text, locale, navigator );
        }
        throw new FunctionCallException( "lower-case() requires at least one argument." );
    }

    /**
     * Converts the given string value to lower case using an optional Locale
     * 
     * @param strArg the value which gets converted to a String
     * @param locale the Locale to use for the conversion or null if the
     *          default should be used
     * @param nav the Navigator to use
     */
    public static String evaluate(Object strArg,
                                  Locale locale,
                                  Navigator nav)
    {

        String str   = StringFunction.evaluate( strArg,
                                                nav );
        if (locale != null)
        {
            return str.toLowerCase(locale);
        }
        else 
        {
            return  str.toLowerCase();
        }
    }
}
