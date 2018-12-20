package com.zhidejiaoyu.common.utils.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * FTP工具类
 * 
 * @author dq
 */
@Slf4j
public class FtpUtil {
	private FTPClient ftpClient = null;

	@Value("${ftp.url}")
	private String server;

	@Value("${ftp.port}")
	private int port;

	@Value("${ftp.user}")
	private String userName;

	@Value("${ftp.pass}")
	private String userPassword;

    @Value("${ftp.prefix}")
    private String prefix;

	/**
	 * 连接服务器
	 * 
	 * @return 连接成功与否 true:成功， false:失败
	 */
	public boolean open() {
		if (ftpClient != null && ftpClient.isConnected()) {
			return true;
		}
		try {
			ftpClient = new FTPClient();
			// 连接
			ftpClient.connect(this.server, this.port);
			ftpClient.login(this.userName, this.userPassword);
			setFtpClient(ftpClient);
			// 检测连接是否成功
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				this.close();
				System.err.println("FTP server refused connection.");
				System.exit(1);
			}
            // 设置上传模式.binally or ascii
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // 设置新建的目录权限为 644
            ftpClient.sendCommand("site umask 022");
            ftpClient.enterLocalPassiveMode();
			return true;
		} catch (Exception ex) {
			this.close();
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 层层切换工作目录
	 * 
	 * @param ftpPath
	 *            目的目录
	 * @return 切换结果
	 */
    private boolean changeDir(String ftpPath) {
		if (!ftpClient.isConnected()) {
			return false;
		}
		try {
			// 将路径中的斜杠统一
			char[] chars = ftpPath.toCharArray();
			StringBuffer sb = new StringBuffer(256);
            for (char aChar : chars) {
                if ('\\' == aChar) {
                    sb.append('/');
                } else {
                    sb.append(aChar);
                }
            }
			ftpPath = sb.toString();
			if (!ftpPath.contains("/")) {
				// 只有一层目录
				ftpClient.changeWorkingDirectory(new String(ftpPath.getBytes(), "iso-8859-1"));
			} else {
				// 多层目录循环创建
				String[] paths = ftpPath.split("/");
                for (String path : paths) {
                    ftpClient.changeWorkingDirectory(new String(path.getBytes(), "iso-8859-1"));
                }
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 循环创建目录，并且创建完目录后，设置工作目录为当前创建的目录下
	 * 
	 * @param ftpPath
	 *            需要创建的目录
	 * @return
	 */
    private boolean mkDir(String ftpPath) {
		if (!ftpClient.isConnected()) {
			return false;
		}
		try {
			// 将路径中的斜杠统一
			char[] chars = ftpPath.toCharArray();
			StringBuffer sbStr = new StringBuffer(256);
            for (char aChar : chars) {
                if ('\\' == aChar) {
					sbStr.append('/');
				} else {
                    sbStr.append(aChar);
				}
			}
			ftpPath = sbStr.toString();

			if (ftpPath.indexOf('/') == -1) {
				// 只有一层目录
				ftpClient.makeDirectory(new String(ftpPath.getBytes(), "iso-8859-1"));
				ftpClient.changeWorkingDirectory(new String(ftpPath.getBytes(), "iso-8859-1"));
			} else {
				// 多层目录循环创建
				String[] paths = ftpPath.split("/");
                for (String path : paths) {
                    ftpClient.makeDirectory(new String(path.getBytes(), "iso-8859-1"));
                    ftpClient.changeWorkingDirectory(new String(path.getBytes(), "iso-8859-1"));
                }
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 上传文件到FTP服务器
	 * 
	 * @param file
	 *            上传到服务器的文件
	 * @param ftpDirectory
	 *            FTP目录如:/path1/pathb2/,如果目录不存在会自动创建目录
	 * @param fileName 
	 * 			      指定上传图片名, 该字段如果为null默认uuid生成的图片名
	 * @return 上传到服务器上的文件名
	 */
	public String upload(MultipartFile file, String ftpDirectory, String fileName) {
		this.open();
		if (!ftpClient.isConnected()) {
			return null;
		}
		// 文件后缀名
		String suffix = "";
		if (Objects.requireNonNull(file.getOriginalFilename()).length() > 0) {
			suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		}
		String ftpFileName;
		if(StringUtils.isNotBlank(fileName)) {
			ftpFileName = fileName + suffix;
		}else {
			ftpFileName = UUID.randomUUID().toString().replace("-","") + suffix;
		}
		boolean flag = false;
		if (ftpClient != null) {
			FileInputStream fis = null;
			try {
				fis = (FileInputStream) file.getInputStream();
                changeDir(ftpDirectory);

				ftpClient.setBufferSize(100000);
				ftpClient.setControlEncoding("UTF-8");
				// 设置文件类型（二进制）
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				// 上传
				flag = ftpClient.storeFile(new String(ftpFileName.getBytes(), "iso-8859-1"), fis);
			} catch (Exception e) {
				this.close();
				e.printStackTrace();
				return null;
			} finally {
				try {
                    if (fis != null) {
                        fis.close();
                    }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag ? ftpFileName : null;
	}

    /**
     * 批量上传文件
     *
     * @param remotePath 文件上传目标路径
     * @param files
     * @return
     * @throws IOException
     */
    public List<String> uploadFile(String remotePath, MultipartFile[] files) {
        FileInputStream fis = null;
        //连接FTP服务器
        this.open();
        if (!ftpClient.isConnected()) {
            return null;
        }
        List<String> fileNames = new ArrayList<>(files.length);
        try {
            ftpClient.changeWorkingDirectory(remotePath);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            String ftpFileName;
            for (MultipartFile fileItem : files) {
                // 文件后缀名
                String suffix = "";
                if (fileItem.getOriginalFilename() != null && fileItem.getOriginalFilename().length() > 0) {
                    suffix = fileItem.getOriginalFilename().substring(fileItem.getOriginalFilename().lastIndexOf("."));
                }
                ftpFileName = UUID.randomUUID() + suffix;
                fileNames.add(ftpFileName);
                fis = (FileInputStream) fileItem.getInputStream();
                ftpClient.storeFile(new String(ftpFileName.getBytes(), "iso-8859-1"), fis);
            }
        } catch (IOException e) {
            log.error("上传文件异常", e);
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.close();
        }
        return fileNames;
    }

	/**
	 * 从FTP服务器上下载文件
	 *
	 * @param ftpDirectoryAndFileName
	 *            ftp服务器文件路径，以/dir形式开始
	 * @param localDirectoryAndFileName
	 *            保存到本地的目录
	 * @return
	 */
	public boolean get(String ftpDirectoryAndFileName, String localDirectoryAndFileName) {
		if (!ftpClient.isConnected()) {
			return false;
		}
		ftpClient.enterLocalPassiveMode(); // Use passive mode as default
		try {
			// 将路径中的斜杠统一
			char[] chars = ftpDirectoryAndFileName.toCharArray();
			StringBuffer sbStr = new StringBuffer(256);
			for (int i = 0; i < chars.length; i++) {
				if ('\\' == chars[i]) {
					sbStr.append('/');
				} else {
					sbStr.append(chars[i]);
				}
			}
			ftpDirectoryAndFileName = sbStr.toString();
			String filePath = ftpDirectoryAndFileName.substring(0, ftpDirectoryAndFileName.lastIndexOf("/"));
			String fileName = ftpDirectoryAndFileName.substring(ftpDirectoryAndFileName.lastIndexOf("/") + 1);
			this.changeDir(filePath);
			ftpClient.retrieveFile(new String(fileName.getBytes(), "iso-8859-1"),
					new FileOutputStream(localDirectoryAndFileName));
			// file
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 返回FTP目录下的文件列表
	 *
	 * @param pathName
	 * @return
	 */
	public String[] getFileNameList(String pathName) {
		try {
			return ftpClient.listNames(pathName);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 删除FTP上的文件
	 *
	 * @param ftpDirAndFileName
	 *            路径开头不能加/，比如应该是test/filename1
	 * @return
	 */
	public boolean deleteFile(String ftpDirAndFileName) {
		if (!ftpClient.isConnected()) {
			return false;
		}
		try {
			return ftpClient.deleteFile(ftpDirAndFileName);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除FTP目录
	 *
	 * @param ftpDirectory
	 * @return
	 */
	public boolean deleteDirectory(String ftpDirectory) {
		if (!ftpClient.isConnected()) {
			return false;
		}
		try {
			return ftpClient.removeDirectory(ftpDirectory);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 关闭链接
	 */
	public void close() {
		try {
			if (ftpClient != null && ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

    /**
     * 上传好声音音频文件到FTP服务器
     *
     * @param file
     *            上传到服务器的文件
     * @param ftpDirectory
     *            FTP目录如:/path1/pathb2/,如果目录不存在会自动创建目录
     * @return 上传到服务器上的文件名
     */
    public String uploadGoodVoice(MultipartFile file, String ftpDirectory) {
        this.open();
        if (!ftpClient.isConnected()) {
            return null;
        }
        // 文件后缀名
        String ftpFileName  = UUID.randomUUID().toString().replace("-","") + ".mp3";
        boolean flag = false;
        if (ftpClient != null) {
            FileInputStream fis = null;
            try {
                fis = (FileInputStream) file.getInputStream();
                changeDir(ftpDirectory);

                ftpClient.setBufferSize(100000);
                ftpClient.setControlEncoding("UTF-8");
                // 设置文件类型（二进制）
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                // 上传
                flag = ftpClient.storeFile(new String(ftpFileName.getBytes(), "iso-8859-1"), fis);
            } catch (Exception e) {
                this.close();
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag ? ftpFileName : null;
    }
}