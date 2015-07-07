package com.mofang.feed.init.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.ThreadRepliesRewardConstant;
import com.mofang.feed.init.AbstractInitializer;
import com.mofang.feed.model.external.ThreadRepliesRewardConfig;

public class ThreadRepliesRewardConfigInitializer extends AbstractInitializer {

	@Override
	public void load() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(
				GlobalConfig.THREAD_REPLIES_REWARD_CONFIG_PATH));
		try {
			List<Properties> sections = new ArrayList<Properties>();
			Properties current = new Properties();
			String line;
			int index = 0;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(!line.equals("")) {
					index++;
				}
				if(line.matches(".*=.*") && index <= 6) {
					int idx = line.indexOf('=');
					String name = line.substring(0, idx).trim();
					String value = line.substring(idx + 1).trim();
					current.setProperty(name, value);
				}
				if(index == 6){
					index = 0;
					sections.add(current);
					current = new Properties();
				}
			}
			
			int size = sections.size();
			List<ThreadRepliesRewardConfig> list = new ArrayList<ThreadRepliesRewardConfig>(size);
			for(int idx = 0; idx < size ; idx++) {
				Properties p = sections.get(idx);
				ThreadRepliesRewardConfig config = new ThreadRepliesRewardConfig();
				config.level = Integer.valueOf(p.getProperty("level"));
				config.repliesRangeMin = Integer.valueOf(p.getProperty("repliesRangeMin"));
				config.repliesRangeMax = Integer.valueOf(p.getProperty("repliesRangeMax"));
				if(config.repliesRangeMax ==  -1) {
					config.repliesRangeMax = Integer.MAX_VALUE;
				}
				config.exp = Integer.valueOf(p.getProperty("exp"));
				config.randomMin = Integer.valueOf(p.getProperty("randomMin"));
				config.randomMax = Integer.valueOf(p.getProperty("randomMax"));
				list.add(config);
			}
			Collections.sort(list);
			ThreadRepliesRewardConstant.CONFIGS = list;
		} finally {
			reader.close();
		}
		
	}

}
