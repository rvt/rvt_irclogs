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

package nl.rvantwisk.jahia.irclogs.schedules;

import org.apache.log4j.Logger;
import org.jahia.services.scheduler.SchedulerService;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created with IntelliJ IDEA.
 *
 * TODO: TO decide if we still need it
 *
 * User: rvt
 * Date: 11/19/12
 * Time: 9:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParseAtStartup implements InitializingBean {
    private static final Logger logger = Logger.getLogger(ParseAtStartup.class);

    private SchedulerService schedulerService;

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void afterPropertiesSet() throws Exception {
        /*
        JobDetail job = new JobDetail();
        job.setName("IRCJobRunner");
        job.setGroup("IRCJobRunner");
        job.setJobClass(IRCJobRunner.class);
        schedulerService.scheduleJobNow(job); */

        logger.info("ParseAtStartup initialised");
    }

}
