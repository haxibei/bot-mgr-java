package com.ruoyi.system.config;

import com.ruoyi.common.mp.config.AbstractMybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author ruoyi
 * @since 1.0
 */
@Configuration
@EnableTransactionManagement
public class SystemMybatisPlusConfig
		extends AbstractMybatisPlusConfig {

//	@Resource
//	private RedisConnectionFactory connectionFactory;
//
//	@Resource
//	private DbMsgReceiver dbMsgReceiver;
//
//	@Bean(name = "dbMsgAdapter")
//	public MessageListenerAdapter getDbMsgAdapter(){
//		return new MessageListenerAdapter(dbMsgReceiver);
//	}
//
//	/**
//	 * 构建redis消息监听器容器
//	 * @return
//	 */
//	@Bean
//	public RedisMessageListenerContainer container( ){
//		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//		container.setConnectionFactory(connectionFactory);
//		//指定不同的方法监听不同的频道
//		container.addMessageListener(getDbMsgAdapter(), new PatternTopic(MessagePublisher.system_db.getChannel()));
//		return container;
//	}
}
