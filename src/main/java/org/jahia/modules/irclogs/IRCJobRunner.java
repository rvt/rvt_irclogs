/*
 * This file is part of the rvt_irclogs project, a Jahia module ti dsplay IRC logs
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

package org.jahia.modules.irclogs;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jahia.api.Constants;
import org.jahia.modules.irclogs.interfaces.ChatlogChannel;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.scheduler.BackgroundJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

/**
 * Quarts Job that execute's on each channel the runJob function
 * User: rvt
 * Date: 11/16/12
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class IRCJobRunner extends BackgroundJob {
    private static final Logger logger = Logger.getLogger(IRCJobRunner.class);


    @Override
    public void executeJahiaJob(JobExecutionContext jobExecutionContext) throws Exception {
        final JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        final String workspace = StringUtils.defaultIfEmpty(data.getString("workspace"),
                Constants.LIVE_WORKSPACE);

        JCRTemplate.getInstance().doExecuteWithSystemSession(null, workspace,
                new JCRCallback<Integer[]>() {
                    @SuppressWarnings("unchecked")
                    public Integer[] doInJCR(JCRSessionWrapper session) throws RepositoryException {

                            Query q = session.getWorkspace().getQueryManager().createQuery("select * from [rvnt:displayIRCLogs]", Query.JCR_SQL2);
                            NodeIterator ni = q.execute().getNodes();
                            while (ni.hasNext()) {
                                JCRNodeWrapper next = (JCRNodeWrapper) ni.next();
                                String channel = next.getProperty("channel").getString();
                                // String directory = next.getProperty("directory").getString();

                                ChatlogChannel clc=ChatLogCache.getInstance().getChannel(channel);
                                if (clc!=null) {
                                    clc.runJob();
                                } else {
                                    // We could create a new channel here... TODO??
                                }
                            }
                            return null;
                    }
                });

        logger.info("Finnished");
    }
}
