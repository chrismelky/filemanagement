package com.softalanta.batchlay.batch;

import com.softalanta.batchlay.domain.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class UserUploadConfig {

    @Bean
    public Job userCsvUploadJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                             ItemReader<User> userCsvFileReader,
                             ItemProcessor<User, User> userUploadProcessor,
                             ItemWriter<User> userUploadWriter){

        Step step = stepBuilderFactory.get("ETL-file-load")
                .<User, User>chunk(100)
                .reader(userCsvFileReader)
                .processor(userUploadProcessor)
                .writer(userUploadWriter)
                .build();
        return jobBuilderFactory.get("ETL-load")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();

    }

    @Bean
    @StepScope
    public FlatFileItemReader<User> userCsvFileReader(@Value("#{jobParameters[filePath]}") String path){
        FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(path));
        flatFileItemReader.setName("CSV-reader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(userCsvLineMapper());

        return  flatFileItemReader;
    }
    @Bean
    public LineMapper<User> userCsvLineMapper(){
        DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setStrict(false);
        delimitedLineTokenizer.setNames("id","name", "dept", "salary");

        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }
}
