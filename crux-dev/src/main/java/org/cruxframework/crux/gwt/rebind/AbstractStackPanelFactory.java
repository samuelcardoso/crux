/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.ProcessingTime;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;



/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="visibleStack", type=Integer.class, processingTime=ProcessingTime.afterAllWidgetsOnView, method="showStack")
})
public abstract class AbstractStackPanelFactory extends ComplexPanelFactory<AbstractStackPanelFactoryContext>
{
	@TagConstraints(tagName="textTitle", type=String.class)
	public abstract static class AbstractTitleTextProcessor extends WidgetChildProcessor<AbstractStackPanelFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, AbstractStackPanelFactoryContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().ensureTextChild(context.getChildElement(), true, context.getWidgetId(), true);
			context.isHtmlTitle = false;
		}
	}
	
	@TagConstraints(tagName="htmlTitle", type=HTMLTag.class)
	public abstract static class AbstractTitleHTMLProcessor extends WidgetChildProcessor<AbstractStackPanelFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, AbstractStackPanelFactoryContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().ensureHtmlChild(context.getChildElement(), true, context.getWidgetId(), context);
			context.isHtmlTitle = true;
		}
	}
	
	@TagConstraints(minOccurs="0", type=AnyWidget.class, autoProcessingEnabled=false)
	public abstract static class AbstractContentWidgetProcessor extends WidgetChildProcessor<AbstractStackPanelFactoryContext> 
	{
		@Override
		public void processChildren(SourcePrinter out,AbstractStackPanelFactoryContext context) throws CruxGeneratorException 
		{
			String child = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			String widget = context.getWidget();
			
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			if (context.title == null)
			{
				out.println(widget+".add("+child+");");
			}
			else
			{
				out.println(widget+".add("+child+", "+context.title+", "+context.isHtmlTitle+");");
			}
			if (childPartialSupport)
			{
				out.println("}");
			}
			context.title = null;
			context.isHtmlTitle = false;
		}	
	}
	
	@Override
    public AbstractStackPanelFactoryContext instantiateContext()
    {
	    return new AbstractStackPanelFactoryContext();
    }
}

class AbstractStackPanelFactoryContext extends WidgetCreatorContext
{
	boolean isHtmlTitle = false;
	String title;
}