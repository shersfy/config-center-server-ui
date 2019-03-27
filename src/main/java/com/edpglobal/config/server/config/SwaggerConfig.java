package com.edpglobal.config.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.edpglobal.config.server.controller.BaseController;
import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
@ConditionalOnProperty(prefix = SwaggerConfigProperties.PREFIX, value = "enabled", havingValue = "true")
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {

        ApiInfo info = new ApiInfoBuilder()
                //页面标题
                .title("Swagger")
                //版本号
                .version("1.0")
                //描述
                .description("API description")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(info)
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage(BaseController.class.getPackage().getName()))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * job_manager v 1.0.7
     *
     * @return
     */
    @Bean
    public Docket jobManager_v107() {
        Predicate<String> predicate = PathSelectors.ant("/job/**");

        ApiInfo apiInfo = new ApiInfoBuilder().title("job_manager接口文档") // 大标题
                .description("1:控制台调用\n" +
                        "2:任务树\n" +
                        "3:组件树\n" +
                        "4:任务拷贝 job->模板复制job->\n" +
                        "5:任务调度接口\n" +
                        "6:任务模板树") // 详细描述
                .termsOfServiceUrl("") // NO terms of service
                .version("1.0.7") // 版本号
                .build();
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo)
                .useDefaultResponseMessages(false)
                .groupName("job_manager")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gouuse.datahub.admin.controller.job.manager"))
                .paths(predicate)
                .build();
    }

}