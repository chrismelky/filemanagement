package com.softalanta.batchlay.controller;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.softalanta.batchlay.domain.FileResource;
import com.softalanta.batchlay.domain.FileResourceDomain;
import com.softalanta.batchlay.domain.User;
import com.softalanta.batchlay.repository.FileResourceRepository;
import com.softalanta.batchlay.repository.UserRepository;
import com.softalanta.batchlay.service.JCloudService;
import com.softalanta.batchlay.service.ReportExportService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRSaver;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class UserController {

    private final
    JobLauncher jobLauncher;

    private final
    Job job;

    private final
    UserRepository userRepository;

    private final
    JCloudService jCloudService;

    private final
    FileResourceRepository fileResourceRepository;

    private final
    ReportExportService reportExportService;

    @Autowired
    public UserController(JobLauncher jobLauncher, Job job, UserRepository userRepository, JCloudService jCloudService, FileResourceRepository fileResourceRepository, ReportExportService reportExportService) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.userRepository = userRepository;
        this.jCloudService = jCloudService;
        this.fileResourceRepository = fileResourceRepository;
        this.reportExportService = reportExportService;
    }

    @GetMapping("/print/{format}")
    public void print(HttpServletResponse response,
                      @PathVariable("format") String format) throws JRException, SQLException, IOException {

        File file = ResourceUtils.getFile("classpath:userReport.jrxml");
        InputStream userReportStream = new FileInputStream(file);

        JasperReport jasperReport = JasperCompileManager.compileReport(userReportStream);
        JRSaver.saveObject(jasperReport, "userReport.jasper");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", "User Report");

        //      Create Temporary file for report
        File tmpFile = null;
        switch (format) {
            case "pdf":
                tmpFile = reportExportService.setParams(parameters).setJasperReport(jasperReport).exportToPdf();
                response.setContentType("application/pdf");
                //  response.addHeader("Content-Disposition", "attachment; filename=users.pdf");
                break;
            case "xls":
                tmpFile = reportExportService.setParams(parameters).setJasperReport(jasperReport).exportXls();
                response.setContentType("application/vnd.ms-excel");
                response.addHeader("Content-Disposition", "attachment; filename=users.xls");
                break;
            case "doc":
                tmpFile = reportExportService.setParams(parameters).setJasperReport(jasperReport).exportDoc();
                response.setContentType("application/msword");
                response.addHeader("Content-Disposition", "attachment; filename=users.doc");
                break;
        }

//      Send response
        Files.copy(tmpFile.toPath(),response.getOutputStream());
        response.getOutputStream().flush();
        tmpFile.delete();
    }

    @PostMapping("/uploadFile")
    public String uploadFile(
            @RequestParam("file")MultipartFile file,
            @RequestParam(defaultValue = "DOCUMENT") FileResourceDomain domain)
            throws IOException {

//      1. Get file name
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

//      2. Get file type
        String contentType = file.getContentType();

//      3. Get file Length
        Long contentLength = file.getSize();
        if(contentLength <= 0){
            //return error message
        }
//      4. Get file md5
        ByteSource byteSource = new MultipartFileByteSource(file);
        String md = byteSource.hash(Hashing.md5()).toString();

//      5. Save file to temporary file
        File tmpFile = Files.createTempFile("fileApp",".tmp").toFile();
        file.transferTo(tmpFile);

        FileResource fileResource = new FileResource(filename,domain,contentLength,contentType, md);

        Long uid = jCloudService.saveFileResource(fileResource, tmpFile);

        return "UPLOADED";
    }

    @PostMapping("/uploadData")
    public BatchStatus uploadData(@RequestParam("file")MultipartFile file) throws IOException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {


        File tmpFile = Files.createTempFile("fileApp",".tmp").toFile();
        file.transferTo(tmpFile);

        JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder()
                .addString("filePath", tmpFile.getAbsolutePath())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters());

        return jobExecution.getStatus();
    }

    @GetMapping("/downloadFile/{uid}")
    public void downloadFile(HttpServletResponse response, @PathVariable("uid") Long uid) throws IOException {
        FileResource fileResource = fileResourceRepository.getOne(uid);

        ByteSource content = jCloudService.getFileContent("documents",fileResource.getUid().toString());

        response.setContentType(fileResource.getContentType());
        IOUtils.copy(content.openStream(),response.getOutputStream());
    }

    @GetMapping("/users")
    public List<User> get(){
        return userRepository.findAll();
    }

    private class MultipartFileByteSource
            extends ByteSource
    {
        private MultipartFile file;

        public MultipartFileByteSource( MultipartFile file )
        {
            this.file = file;
        }

        @Override
        public InputStream openStream() throws IOException
        {
            try
            {
                return file.getInputStream();
            }
            catch ( IOException ioe )
            {
                return null;
            }
        }
    }




}
