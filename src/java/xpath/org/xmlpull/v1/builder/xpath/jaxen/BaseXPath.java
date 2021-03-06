/*
 * $Header: /l/extreme/cvspub/XPP3/java/src/java/xpath/org/xmlpull/v1/builder/xpath/jaxen/BaseXPath.java,v 1.2 2005/08/11 22:44:00 aslom Exp $
 * $Revision: 1.2 $
 * $Date: 2005/08/11 22:44:00 $
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
 * $Id: BaseXPath.java,v 1.2 2005/08/11 22:44:00 aslom Exp $
 */


package org.xmlpull.v1.builder.xpath.jaxen;

import org.xmlpull.v1.builder.xpath.jaxen.expr.Expr;
import org.xmlpull.v1.builder.xpath.jaxen.expr.XPathExpr;
import org.xmlpull.v1.builder.xpath.jaxen.function.BooleanFunction;
import org.xmlpull.v1.builder.xpath.jaxen.function.StringFunction;
import org.xmlpull.v1.builder.xpath.jaxen.function.NumberFunction;

import org.xmlpull.v1.builder.xpath.saxpath.XPathReader;
import org.xmlpull.v1.builder.xpath.saxpath.SAXPathException;
import org.xmlpull.v1.builder.xpath.saxpath.helpers.XPathReaderFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Base functionality for all concrete, implementation-specific XPaths.
 *
 *  <p>
 *  This class provides generic functionality for further-defined
 *  implementation-specific XPaths.
 *  </p>
 *
 *  <p>
 *  If you want to adapt the Jaxen engine so that it can traverse your own
 *  object model then this is a good base class to derive from.
 *  Typically you only really need to provide your own
 *  {@link org.jaxen.Navigator} implementation.
 *  </p>
 *
 *  @see org.jaxen.dom4j.Dom4jXPath XPath for dom4j
 *  @see org.jaxen.jdom.JDOMXPath  XPath for JDOM
 *  @see org.jaxen.dom.DOMXPath   XPath for W3C DOM
 *  @see org.jaxen.exml.ElectricXPath  XPath for Electric XML
 *
 *  @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 *  @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 */
public class BaseXPath implements XPath, Serializable
{
    /** the parsed form of the xpath expression */
    private XPathExpr xpath;
    
    /** the support information and function, namespace and variable contexts */
    private ContextSupport support;

    /** the implementation-specific Navigator for retrieving XML nodes **/
    private Navigator navigator;
    
    /** Construct given an XPath expression string.
     *
     *  @param xpathExpr The XPath expression.
     *
     *  @throws JaxenException if there is a syntax error while
     *          parsing the expression.
     */
    protected BaseXPath(String xpathExpr) throws JaxenException
    {
        try
        {
            XPathReader reader = XPathReaderFactory.createReader();
            
            JaxenHandler handler = new JaxenHandler();
            
            reader.setXPathHandler( handler );
            
            reader.parse( xpathExpr );

            this.xpath = handler.getXPathExpr();
        }
        catch (org.xmlpull.v1.builder.xpath.saxpath.XPathSyntaxException e)
        {
            throw new org.xmlpull.v1.builder.xpath.jaxen.XPathSyntaxException( e.getXPath(),
                                                      e.getPosition(),
                                                      e.getMessage() );
        }
        catch (SAXPathException e)
        {
            throw new JaxenException( e );
        }
    }

    /** Construct given an XPath expression string.
     *
     *  @param xpathExpr The XPath expression.
     *
     *  @param navigator the XML navigator to use
     *
     *  @throws JaxenException if there is a syntax error while
     *          parsing the expression.
     */
    public BaseXPath(String xpathExpr, Navigator navigator) throws JaxenException
    {
        this( xpathExpr );
        this.navigator = navigator;
    }

    /** Evaluate this XPath against a given context.
     *
     *  <p>
     *  The context of evaluation my be a <i>document</i>,
     *  an <i>element</i>, or a set of <i>elements</i>.
     *  </p>
     *
     *  <p>
     *  If the expression evaluates to a single primitive
     *  (String, Number or Boolean) type, it is returned
     *  directly.  Otherwise, the returned value is a
     *  List (a <code>node-set</code>, in the terms of the
     *  specification) of values.
     *  </p>
     *
     *  <p>
     *  When using this method, one must be careful to
     *  test the class of the returned objects, and of
     *  each of the composite members if a <code>List</code>
     *  is returned.  If the returned members are XML entities,
     *  they will be the actual <code>Document</code>,
     *  <code>Element</code> or <code>Attribute</code> objects
     *  as defined by the concrete XML object-model implementation,
     *  directly from the context document.  This <b>does not
     *  return <i>copies</i> of anything</b>, but merely returns
     *  references to entities within the source document.
     *  </p>
     *
     *  @param node The node, nodeset or Context object for evaluation. This value can be null.
     *
     *  @return The result of evaluating the XPath expression
     *          against the supplied context.
     */
    public Object evaluate(Object node) throws JaxenException
    {
        List answer = selectNodes(node);

        if ( answer != null
             &&
             answer.size() == 1 )
        {
            Object first = answer.get(0);

            if ( first instanceof String
                 ||
                 first instanceof Number
                 ||
                 first instanceof Boolean )
            {
                return first;
            }
        }
        return answer;
    }
    
    /** Select all nodes that are selectable by this XPath
     *  expression. If multiple nodes match, multiple nodes
     *  will be returned.
     *
     *  <p>
     *  <b>NOTE:</b> In most cases, nodes will be returned
     *  in document-order, as defined by the XML Canonicalization
     *  specification.  The exception occurs when using XPath
     *  expressions involving the <code>union</code> operator
     *  (denoted with the pipe '|' character).
     *  </p>
     *
     *  @param node The node, nodeset or Context object for evaluation. This value can be null.
     *
     *  @return The <code>node-set</code> of all items selected
     *          by this XPath expression.
     *
     *  @see #selectSingleNode
     */
    public List selectNodes(Object node) throws JaxenException
    {
        Context context = getContext( node );
        
        return selectNodesForContext( context );
    }

    /** Select only the first node that is selectable by this XPath
     *  expression.  If multiple nodes match, only one node will be
     *  returned.
     *
     *  <b>NOTE:</b> In most cases, the selected node will be the first
     *  selectable node in document-order, as defined by the XML Canonicalization
     *  specification.  The exception occurs when using XPath
     *  expressions involving the <code>union</code> operator
     *  (denoted with the pipe '|' character).
     *  </p>
     *
     *  @param node The node, nodeset or Context object for evaluation. This value can be null.
     *
     *  @return The <code>node-set</code> of all items selected
     *          by this XPath expression.
     *
     *  @see #selectNodes
     */
    public Object selectSingleNode(Object node) throws JaxenException
    {
        List results = selectNodes( node );

        if ( results.isEmpty() )
        {
            return null;
        }

        return results.get( 0 );
    }

    public String valueOf(Object node) throws JaxenException
    {
        return stringValueOf( node );
    }

    public String stringValueOf(Object node) throws JaxenException
    {
        Context context = getContext( node );
        
        Object result = selectSingleNodeForContext( context );

        if ( result == null )
        {
            return "";
        }

        return StringFunction.evaluate( result,
                                        context.getNavigator() );
    }

    /** Retrieve a boolean-value interpretation of this XPath
     *  expression when evaluated against a given context.
     *
     *  <p>
     *  The boolean-value of the expression is determined per
     *  the <code>boolean(..)</code> core function as defined
     *  in the XPath specification.  This means that an expression
     *  that selects zero nodes will return <code>false</code>,
     *  while an expression that selects one-or-more nodes will
     *  return <code>true</code>.
     *  </p>
     *
     *  @param node The node, nodeset or Context object for evaluation. This value can be null.
     *
     *  @return The boolean-value interpretation of this expression.
     */
    public boolean booleanValueOf(Object node) throws JaxenException
    {
        Context context = getContext( node );
        
        List result = selectNodesForContext( context );

        if ( result == null )
            return false;

            
        return BooleanFunction.evaluate( result, context.getNavigator() ).booleanValue();
    }

    /** Retrieve a number-value interpretation of this XPath
     *  expression when evaluated against a given context.
     *
     *  <p>
     *  The number-value of the expression is determined per
     *  the <code>number(..)</code> core function as defined
     *  in the XPath specification. This means that if this
     *  expression selects multiple nodes, the number-value
     *  of the first node is returned.
     *  </p>
     *
     *  @param node The node, nodeset or Context object for evaluation. This value can be null.
     *
     *  @return The number-value interpretation of this expression.
     */
    public Number numberValueOf(Object node) throws JaxenException
    {
        Context context = getContext( node );
        
        Object result = selectSingleNodeForContext( context );

        if ( result == null )
        {
            return null;
        }

        return NumberFunction.evaluate( result,
                                        context.getNavigator() );
    }

    // Helpers

    /** Add a namespace prefix-to-URI mapping for this XPath
     *  expression.
     *
     *  <p>
     *  Namespace prefix-to-URI mappings in an XPath are independant
     *  of those used within any document.  Only the mapping explicitly
     *  added to this XPath will be available for resolving the
     *  XPath expression.
     *  </p>
     *
     *  <p>
     *  This is a convenience method for adding mappings to the
     *  default {@link NamespaceContext} in place for this XPath.
     *  If you have installed a specific custom <code>NamespaceContext</code>,
     *  then this method will throw a <code>JaxenException</code>.
     *  </p>
     *
     *  @param prefix The namespace prefix.
     *  @param uri The namespace URI.
     *
     *  @throws JaxenException If a <code>NamespaceContext</code>
     *          used by this XPath has been explicitly installed.
     */
    public void addNamespace(String prefix,
                             String uri) throws JaxenException
    {
        NamespaceContext nsContext = getNamespaceContext();

        if ( nsContext instanceof SimpleNamespaceContext )
        {
            ((SimpleNamespaceContext)nsContext).addNamespace( prefix,
                                                              uri );

            return;
        }

        throw new JaxenException("Operation not permitted while using a custom namespace context.");
    }


    // ------------------------------------------------------------
    // ------------------------------------------------------------
    //     Properties
    // ------------------------------------------------------------
    // ------------------------------------------------------------

    
    /** Set a <code>NamespaceContext</code> for use with this
     *  XPath expression.
     *
     *  <p>
     *  A <code>NamespaceContext</code> is responsible for translating
     *  namespace prefixes within the expression into namespace URIs.
     *  </p>
     *
     *  @param namespaceContext The <code>NamespaceContext</code> to
     *         install for this expression.
     *
     *  @see NamespaceContext
     *  @see NamespaceContext#translateNamespacePrefixToUri
     */
    public void setNamespaceContext(NamespaceContext namespaceContext)
    {
        getContextSupport().setNamespaceContext(namespaceContext);
    }

    /** Set a <code>FunctionContext</code> for use with this XPath
     *  expression.
     *
     *  <p>
     *  A <code>FunctionContext</code> is responsible for resolving
     *  all function calls used within the expression.
     *  </p>
     *
     *  @param functionContext The <code>FunctionContext</code> to
     *         install for this expression.
     *
     *  @see FunctionContext
     *  @see FunctionContext#getFunction
     */
    public void setFunctionContext(FunctionContext functionContext)
    {
        getContextSupport().setFunctionContext(functionContext);
    }

    /** Set a <code>VariableContext</code> for use with this XPath
     *  expression.
     *
     *  <p>
     *  A <code>VariableContext</code> is responsible for resolving
     *  all variables referenced within the expression.
     *  </p>
     *
     *  @param variableContext The <code>VariableContext</code> to
     *         install for this expression.
     *
     *  @see VariableContext
     *  @see VariableContext#getVariableValue
     */
    public void setVariableContext(VariableContext variableContext)
    {
        getContextSupport().setVariableContext(variableContext);
    }

    /** Retrieve the <code>NamespaceContext</code> used by this XPath
     *  expression.
     *
     *  <p>
     *  A <code>FunctionContext</code> is responsible for resolving
     *  all function calls used within the expression.
     *  </p>
     *
     *  <p>
     *  If this XPath expression has not previously had a <code>NamespaceContext</code>
     *  installed, a new default <code>NamespaceContext</code> will be created,
     *  installed and returned.
     *  </p>
     *
     *  @return The <code>NamespaceContext</code> used by this expression.
     *
     *  @see NamespaceContext
     */
    public NamespaceContext getNamespaceContext()
    {
        NamespaceContext answer = getContextSupport().getNamespaceContext();
        if ( answer == null ) {
            answer = createNamespaceContext();
            getContextSupport().setNamespaceContext( answer );
        }
        return answer;
    }

    /** Retrieve the <code>FunctionContext</code> used by this XPath
     *  expression.
     *
     *  <p>
     *  A <code>FunctionContext</code> is responsible for resolving
     *  all function calls used within the expression.
     *  </p>
     *
     *  <p>
     *  If this XPath expression has not previously had a <code>FunctionContext</code>
     *  installed, a new default <code>FunctionContext</code> will be created,
     *  installed and returned.
     *  </p>
     *
     *  @return The <code>FunctionContext</code> used by this expression.
     *
     *  @see FunctionContext
     */
    public FunctionContext getFunctionContext()
    {
        FunctionContext answer = getContextSupport().getFunctionContext();
        if ( answer == null ) {
            answer = createFunctionContext();
            getContextSupport().setFunctionContext( answer );
        }
        return answer;
    }

    /** Retrieve the <code>VariableContext</code> used by this XPath
     *  expression.
     *
     *  <p>
     *  A <code>VariableContext</code> is responsible for resolving
     *  all variables referenced within the expression.
     *  </p>
     *
     *  <p>
     *  If this XPath expression has not previously had a <code>VariableContext</code>
     *  installed, a new default <code>VariableContext</code> will be created,
     *  installed and returned.
     *  </p>
     *
     *  @return The <code>VariableContext</code> used by this expression.
     *
     *  @see VariableContext
     */
    public VariableContext getVariableContext()
    {
        VariableContext answer = getContextSupport().getVariableContext();
        if ( answer == null ) {
            answer = createVariableContext();
            getContextSupport().setVariableContext( answer );
        }
        return answer;
    }
    
    
    /** Retrieve the root expression of the internal
     *  compiled form of this XPath expression.
     *
     *  <p>
     *  Internally, Jaxen maintains a form of Abstract Syntax
     *  Tree (AST) to represent the structure of the XPath expression.
     *  This is normally not required during normal consumer-grade
     *  usage of Jaxen.  This method is provided for hard-core users
     *  who wish to manipulate or inspect a tree-based version of
     *  the expression.
     *  </p>
     *
     *  @return The root of the AST of this expression.
     */
    public Expr getRootExpr()
    {
        return xpath.getRootExpr();
    }
    
    /** Return the normalized string of this XPath expression.
     *
     *  <p>
     *  During parsing, the XPath expression is normalized,
     *  removing abbreviations and other convenience notation.
     *  This method returns the fully normalized representation
     *  of the original expression.
     *  </p>
     *
     *  @return The normalized XPath expression string.
     */
    public String toString()
    {
        return this.xpath.getText();
    }

    /** Returns the string version of this xpath.
     *
     *  @return The normalized XPath expression string.
     *
     *  @see #toString
     */
    public String debug()
    {
        return this.xpath.toString();
    }
    
    // ------------------------------------------------------------
    // ------------------------------------------------------------
    //     Implementation methods
    // ------------------------------------------------------------
    // ------------------------------------------------------------

    
    /** Create a {@link Context} wrapper for the provided
     *  implementation-specific object.
     *
     *  @param node The implementation-specific object
     *         to be used as the context.
     *
     *  @return A <code>Context</code> wrapper around the object.
     */
    protected Context getContext(Object node)
    {
        if ( node instanceof Context )
        {
            return (Context) node;
        }

        Context fullContext = new Context( getContextSupport() );

        if ( node instanceof List )
        {
            fullContext.setNodeSet( (List) node );
        }
        else
        {
            List list = new ArrayList( 1 );

            list.add( node );

            fullContext.setNodeSet( list );
        }

        return fullContext;
    }

    /** Retrieve the {@link ContextSupport} aggregation of
     *  <code>NamespaceContext</code>, <code>FunctionContext</code>,
     *  <code>VariableContext</code>, and {@link Navigator}.
     *
     *  @return Aggregate <code>ContextSupport</code> for this
     *          XPath expression.
     */
    protected ContextSupport getContextSupport()
    {
        if ( support == null )
        {
            support = new ContextSupport(
                createNamespaceContext(),
                createFunctionContext(),
                createVariableContext(),
                getNavigator()
            );
        }

        return support;
    }

    /** Retrieve the XML object-model-specific {@link Navigator}
     *  for us in evaluating this XPath expression.
     *
     *  @return The implementation-specific <code>Navigator</code>.
     */
    public Navigator getNavigator()
    {
        return navigator;
    }
    
    

    // ------------------------------------------------------------
    // ------------------------------------------------------------
    //     Factory methods for default contexts
    // ------------------------------------------------------------
    // ------------------------------------------------------------

    /** Create a default <code>FunctionContext</code>.
     *
     *  @return The default <code>FunctionContext</code>.
     */
    protected FunctionContext createFunctionContext()
    {
        return XPathFunctionContext.getInstance();
    }
    
    /** Create a default <code>NamespaceContext</code>.
     *
     *  @return A default <code>NamespaceContext</code> instance.
     */
    protected NamespaceContext createNamespaceContext()
    {
        return new SimpleNamespaceContext();
    }
    
    /** Create a default <code>VariableContext</code>.
     *
     *  @return A default <code>VariableContext</code> instance.
     */
    protected VariableContext createVariableContext()
    {
        return new SimpleVariableContext();
    }
    
    /** Select all nodes that are selectable by this XPath
     *  expression on the given Context object.
     *  If multiple nodes match, multiple nodes
     *  will be returned.
     *
     *  <p>
     *  <b>NOTE:</b> In most cases, nodes will be returned
     *  in document-order, as defined by the XML Canonicalization
     *  specification.  The exception occurs when using XPath
     *  expressions involving the <code>union</code> operator
     *  (denoted with the pipe '|' character).
     *  </p>
     *
     *  @param context is the Context which gets evaluated.
     *
     *  @return The <code>node-set</code> of all items selected
     *          by this XPath expression.
     *
     */
    protected List selectNodesForContext(Context context) throws JaxenException
    {
        return this.xpath.asList( context );
    }
    
    /** Select only the first node that is selectable by this XPath
     *  expression.  If multiple nodes match, only one node will be
     *  returned.
     *
     *  <b>NOTE:</b> In most cases, the selected node will be the first
     *  selectable node in document-order, as defined by the XML Canonicalization
     *  specification.  The exception occurs when using XPath
     *  expressions involving the <code>union</code> operator
     *  (denoted with the pipe '|' character).
     *  </p>
     *
     *  @param context is the Context which gets evaluated.
     *
     *  @return The <code>node-set</code> of all items selected
     *          by this XPath expression.
     *
     *  @see #selectNodesForContext
     */
    protected Object selectSingleNodeForContext(Context context) throws JaxenException
    {
        List results = selectNodesForContext( context );

        if ( results.isEmpty() )
        {
            return null;
        }

        return results.get( 0 );
    }
}

