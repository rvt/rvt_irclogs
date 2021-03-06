/*
 * This file is part of the rvt_irclogs project, a Jahia module to display IRC logs
 *
 * Copyright (C) 2010 R. van Twisk (rvt@dds.nl)
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation and appearing in the file gpl-2.0.txt included in the
 * packaging of this file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * This copyright notice MUST APPEAR in all copies of the script!
 */

package nl.rvantwisk.jahia.irclogs.filters;

import net.htmlparser.jericho.*;
import org.apache.commons.lang.StringUtils;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.cache.AggregateCacheFilter;
import org.jahia.services.templates.JahiaTemplateManagerService;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.utils.ScriptEngineUtils;
import org.jahia.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Filter the generate a new title for a specific log entry of a IRC logfile
 * <p>
 * User: rvt
 * Date: 11/18/12
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class IRCLogPageTitleFilter extends AbstractFilter {
    private static Logger logger = LoggerFactory.getLogger(IRCLogPageTitleFilter.class);

    private String title = null;
    private DateTimeFormatter dateFormatter = null;
    private String template;
    private ScriptEngineUtils scriptEngineUtils;
    private String resolvedTemplate;
    private String localesToTry;

    @Override
    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        JahiaUserManagerService f;
        String out = previousOut;
        String script = getResolvedTemplate();
        if (script != null) {
            Source source = new Source(previousOut);
            OutputDocument outputDocument = new OutputDocument(source);
            List<Element> headElementList = source.getAllElements(HTMLElementName.TITLE);
            for (Element element : headElementList) {
                final EndTag bodyEndTag = element.getEndTag();
                final StartTag bodyStartTag = element.getStartTag();
                String extension = StringUtils.substringAfterLast(template, ".");
                ScriptEngine scriptEngine = scriptEngineUtils.scriptEngine(extension);
                ScriptContext scriptContext = new irclogsScriptContext();
                final Bindings bindings = scriptEngine.createBindings();
                bindings.put("resource", resource);
                scriptContext.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);

                // The following binding is necessary for Javascript, which doesn't offer a console by default.
                bindings.put("out", new PrintWriter(scriptContext.getWriter()));

                // Parameters needed for title replacing
                bindings.put("orgTitle", outputDocument.toString().substring(bodyStartTag.getEnd(), bodyEndTag.getBegin()));
                bindings.put("dateFormatter", dateFormatter.withLocale(renderContext.getMainResourceLocale()));
                bindings.put("title", getTitle());
                bindings.put("localesToTry", getLocalesToTry());
                bindings.put("renderContext", renderContext);


                scriptEngine.eval(script, scriptContext);
                StringWriter writer = (StringWriter) scriptContext.getWriter();
                final String irclogsScript = writer.toString();
                if (StringUtils.isNotBlank(irclogsScript)) {
                    outputDocument.replace(bodyStartTag.getEnd(), bodyEndTag.getBegin() + 1,
                            AggregateCacheFilter.removeEsiTags(irclogsScript) + "<");
                }
                break; // avoid to loop if for any reasons multiple body in the page
            }
            out = outputDocument.toString().trim();
        }

        return out;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateFormat() {
        return dateFormatter.toString();
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    protected String getResolvedTemplate() throws IOException {
        if (resolvedTemplate == null) {
            resolvedTemplate = WebUtils.getResourceAsString(template);
            if (resolvedTemplate == null) {
                logger.warn("Unable to lookup template at {}", template);
            }
        }
        return resolvedTemplate;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof JahiaTemplateManagerService.TemplatePackageRedeployedEvent) {
            resolvedTemplate = null;
        }
    }

    public void setScriptEngineUtils(ScriptEngineUtils scriptEngineUtils) {
        this.scriptEngineUtils = scriptEngineUtils;
    }

    public void setLocalesToTry(String localesToTry) {
        this.localesToTry = localesToTry;
    }

    public String getLocalesToTry() {
        return localesToTry;
    }

    class irclogsScriptContext extends SimpleScriptContext {
        private Writer writer = null;

        /**
         * {@inheritDoc}
         */
        @Override
        public Writer getWriter() {
            if (writer == null) {
                writer = new StringWriter();
            }
            return writer;
        }
    }
}
