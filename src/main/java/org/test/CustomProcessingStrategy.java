package org.test;

import java.io.Serializable;
import java.util.List;

import org.mule.api.MuleContext;
import org.mule.api.config.ThreadingProfile;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorChainBuilder;
import org.mule.api.processor.ProcessingStrategy;
import org.mule.api.store.QueueStore;
import org.mule.config.ChainedThreadingProfile;
import org.mule.config.QueueProfile;
import org.mule.management.stats.QueueStatistics;
import org.mule.management.stats.QueueStatisticsAware;
import org.mule.processor.AsyncInterceptingMessageProcessor;
import org.mule.processor.SedaStageInterceptingMessageProcessor;
import org.mule.processor.strategy.SynchronousProcessingStrategy;
import org.mule.util.concurrent.ThreadNameHelper;

public class CustomProcessingStrategy implements ProcessingStrategy , QueueStatisticsAware
{
    protected ProcessingStrategy synchronousProcessingStrategy = new SynchronousProcessingStrategy();
    protected QueueStatistics queueStatistics;
    
    public Integer maxThreads;
    public Integer minThreads;
    public Integer maxBufferSize;
    public Long threadTTL;
    public Long threadWaitTimeout;
    public Integer poolExhaustedAction;
    public Boolean doThreading;

    protected QueueStore<Serializable> queueStore = null;
    public Integer maxQueueSize;
    public Integer queueTimeout;
    
    
	@Override
	public void configureProcessors(List<MessageProcessor> processors,
			org.mule.api.processor.StageNameSource nameSource, MessageProcessorChainBuilder chainBuilder,
			MuleContext muleContext) {
        if (processors.size() > 0)
        {
            chainBuilder.chain(createAsyncMessageProcessor(nameSource, muleContext));
            synchronousProcessingStrategy.configureProcessors(processors, nameSource, chainBuilder,
                muleContext);
        }
	}
	

	
    protected AsyncInterceptingMessageProcessor createAsyncMessageProcessor(org.mule.api.processor.StageNameSource nameSource,
            MuleContext muleContext)
	{
        QueueProfile queueProfile = new QueueProfile(maxQueueSize, queueStore);

        return new SedaStageInterceptingMessageProcessor(getThreadPoolName(nameSource.getName(), muleContext), 
        		nameSource.getName(), 
        		queueProfile, 
        		queueTimeout, 
        		createThreadingProfile(muleContext), 
        		queueStatistics, muleContext);
//		return new AsyncInterceptingMessageProcessor(createThreadingProfile(muleContext), getThreadPoolName(
//		nameSource.getName(), muleContext), muleContext.getConfiguration().getShutdownTimeout());

	}

    protected ThreadingProfile createThreadingProfile(MuleContext muleContext)
    {
        ThreadingProfile threadingProfile = new ChainedThreadingProfile(muleContext.getDefaultThreadingProfile());
        if (maxThreads != null)
        {
            threadingProfile.setMaxThreadsActive(maxThreads);
        }
        if (minThreads != null)
        {
            threadingProfile.setMaxThreadsIdle(minThreads);
        }
        if (maxBufferSize != null)
        {
            threadingProfile.setMaxBufferSize(maxBufferSize);
        }
        if (threadTTL != null)
        {
            threadingProfile.setThreadTTL(threadTTL);
        }
        if (threadWaitTimeout != null)
        {
            threadingProfile.setThreadWaitTimeout(threadWaitTimeout);
        }
        if (poolExhaustedAction != null)
        {
            threadingProfile.setPoolExhaustedAction(poolExhaustedAction);
        }
        threadingProfile.setMuleContext(muleContext);
        return threadingProfile;
    }

    protected String getThreadPoolName(String stageName, MuleContext muleContext)
    {
        return ThreadNameHelper.flow(muleContext, stageName);
    }



	public void setSynchronousProcessingStrategy(ProcessingStrategy synchronousProcessingStrategy) {
		this.synchronousProcessingStrategy = synchronousProcessingStrategy;
	}



	public void setMaxThreads(Integer maxThreads) {
		this.maxThreads = maxThreads;
	}



	public void setMinThreads(Integer minThreads) {
		this.minThreads = minThreads;
	}



	public void setMaxBufferSize(Integer maxBufferSize) {
		this.maxBufferSize = maxBufferSize;
	}



	public void setThreadTTL(Long threadTTL) {
		this.threadTTL = threadTTL;
	}



	public void setThreadWaitTimeout(Long threadWaitTimeout) {
		this.threadWaitTimeout = threadWaitTimeout;
	}



	public void setPoolExhaustedAction(Integer poolExhaustedAction) {
		this.poolExhaustedAction = poolExhaustedAction;
	}



	public void setDoThreading(Boolean doThreading) {
		this.doThreading = doThreading;
	}



	public void setMaxQueueSize(Integer maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}



	public void setQueueTimeout(Integer queueTimeout) {
		this.queueTimeout = queueTimeout;
	}



	public void setQueueStore(QueueStore<Serializable> queueStore) {
		this.queueStore = queueStore;
	}


    public QueueStatistics getQueueStatistics()
    {
        return queueStatistics;
    }

    @Override
    public void setQueueStatistics(QueueStatistics queueStatistics)
    {
        this.queueStatistics = queueStatistics;
    }

}
