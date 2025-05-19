package com.example.ptweb.autoconfig;

import com.example.ptweb.other.PeersCleanup;
import com.example.ptweb.other.TorrentPromotionAdjustJob;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
@EnableScheduling
public class QuartzConfig {
    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(PeersCleanup.class)
                .withIdentity("peers_cleanup")
                .withDescription("Peers Cleanup")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(30))
                .startNow()
                .build();
    }
    @Bean
    public JobDetail torrentPolicyAdjustJobDetail() {
        return JobBuilder.newJob(TorrentPromotionAdjustJob.class)
                .withIdentity("torrent_policy_adjust")
                .withDescription("Adjust Promotion Policy")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger torrentPolicyAdjustTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(torrentPolicyAdjustJobDetail())
                .withSchedule(SimpleScheduleBuilder.repeatHourlyForever(1)) // 每小时运行一次
                .startNow()
                .build();
    }


    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected @NotNull Object createJobInstance(@NotNull TriggerFiredBundle bundle) throws Exception {
            Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }
}
