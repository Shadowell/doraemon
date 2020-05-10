/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shadowell.doraemon.core.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import static com.shadowell.doraemon.core.util.Preconditions.checkNotNull;

/**
 * This is a utility class to deal files and directories. Contains utilities for recursive
 * deletion and creation of temporary files.
 */
public final class FileUtils {

	/** Global lock to prevent concurrent directory deletes under Windows. */
	private static final Object WINDOWS_DELETE_LOCK = new Object();

	/** The alphabet to construct the random part of the filename from. */
	private static final char[] ALPHABET =
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f' };

	/** The length of the random part of the filename. */
	private static final int RANDOM_FILE_NAME_LENGTH = 12;

	/**
	 * The maximum size of array to allocate for reading.
	 */
	private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

	/** The size of the buffer used for reading. */
	private static final int BUFFER_SIZE = 4096;

	private static final String JAR_FILE_EXTENSION = "jar";

	public static final String CLASS_FILE_EXTENSION = "class";

	public static final String PACKAGE_SEPARATOR = ".";


	// ------------------------------------------------------------------------

	public static void writeCompletely(WritableByteChannel channel, ByteBuffer src) throws IOException {
		while (src.hasRemaining()) {
			channel.write(src);
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Lists the given directory in a resource-leak-safe way.
	 */
	public static java.nio.file.Path[] listDirectory(java.nio.file.Path directory) throws IOException {
		try (Stream<java.nio.file.Path> stream = Files.list(directory)) {
			return stream.toArray(java.nio.file.Path[]::new);
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Constructs a random filename with the given prefix and
	 * a random part generated from hex characters.
	 *
	 * @param prefix
	 *        the prefix to the filename to be constructed
	 * @return the generated random filename with the given prefix
	 */
	public static String getRandomFilename(final String prefix) {
		final Random rnd = new Random();
		final StringBuilder stringBuilder = new StringBuilder(prefix);

		for (int i = 0; i < RANDOM_FILE_NAME_LENGTH; i++) {
			stringBuilder.append(ALPHABET[rnd.nextInt(ALPHABET.length)]);
		}

		return stringBuilder.toString();
	}

	// ------------------------------------------------------------------------
	//  Simple reading and writing of files
	// ------------------------------------------------------------------------

	public static String readFile(File file, String charsetName) throws IOException {
		byte[] bytes = readAllBytes(file.toPath());
		return new String(bytes, charsetName);
	}

	public static String readFileUtf8(File file) throws IOException {
		return readFile(file, "UTF-8");
	}

	public static void writeFile(File file, String contents, String encoding) throws IOException {
		byte[] bytes = contents.getBytes(encoding);
		Files.write(file.toPath(), bytes, StandardOpenOption.WRITE);
	}

	public static void writeFileUtf8(File file, String contents) throws IOException {
		writeFile(file, contents, "UTF-8");
	}

	/**
	 * Reads all the bytes from a file. The method ensures that the file is
	 * closed when all bytes have been read or an I/O error, or other runtime
	 * exception, is thrown.
	 *
	 * <p>This is an implementation that follow {@link Files#readAllBytes(java.nio.file.Path)},
	 * and the difference is that it limits the size of the direct buffer to avoid
	 * direct-buffer OutOfMemoryError. When {@link Files#readAllBytes(java.nio.file.Path)}
	 * or other interfaces in java API can do this in the future, we should remove it.
	 *
	 * @param path
	 *        the path to the file
	 * @return a byte array containing the bytes read from the file
	 *
	 * @throws IOException
	 *         if an I/O error occurs reading from the stream
	 * @throws OutOfMemoryError
	 *         if an array of the required size cannot be allocated, for
	 *         example the file is larger that {@code 2GB}
	 */
	public static byte[] readAllBytes(java.nio.file.Path path) throws IOException {
		try (SeekableByteChannel channel = Files.newByteChannel(path);
			InputStream in = Channels.newInputStream(channel)) {

			long size = channel.size();
			if (size > (long) MAX_BUFFER_SIZE) {
				throw new OutOfMemoryError("Required array size too large");
			}

			return read(in, (int) size);
		}
	}

	/**
	 * Reads all the bytes from an input stream. Uses {@code initialSize} as a hint
	 * about how many bytes the stream will have and uses {@code directBufferSize}
	 * to limit the size of the direct buffer used to read.
	 *
	 * @param source
	 *        the input stream to read from
	 * @param initialSize
	 *        the initial size of the byte array to allocate
	 * @return a byte array containing the bytes read from the file
	 *
	 * @throws IOException
	 *         if an I/O error occurs reading from the stream
	 * @throws OutOfMemoryError
	 *         if an array of the required size cannot be allocated
	 */
	private static byte[] read(InputStream source, int initialSize) throws IOException {
		int capacity = initialSize;
		byte[] buf = new byte[capacity];
		int nread = 0;
		int n;

		for (; ;) {
			// read to EOF which may read more or less than initialSize (eg: file
			// is truncated while we are reading)
			while ((n = source.read(buf, nread, Math.min(capacity - nread, BUFFER_SIZE))) > 0) {
				nread += n;
			}

			// if last call to source.read() returned -1, we are done
			// otherwise, try to read one more byte; if that failed we're done too
			if (n < 0 || (n = source.read()) < 0) {
				break;
			}

			// one more byte was read; need to allocate a larger buffer
			if (capacity <= MAX_BUFFER_SIZE - capacity) {
				capacity = Math.max(capacity << 1, BUFFER_SIZE);
			} else {
				if (capacity == MAX_BUFFER_SIZE) {
					throw new OutOfMemoryError("Required array size too large");
				}
				capacity = MAX_BUFFER_SIZE;
			}
			buf = Arrays.copyOf(buf, capacity);
			buf[nread++] = (byte) n;
		}
		return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
	}

	// ------------------------------------------------------------------------
	//  Deleting directories on standard File Systems
	// ------------------------------------------------------------------------

	/**
	 * Removes the given file or directory recursively.
	 *
	 * <p>If the file or directory does not exist, this does not throw an exception, but simply does nothing.
	 * It considers the fact that a file-to-be-deleted is not present a success.
	 *
	 * <p>This method is safe against other concurrent deletion attempts.
	 *
	 * @param file The file or directory to delete.
	 *
	 * @throws IOException Thrown if the directory could not be cleaned for some reason, for example
	 *                     due to missing access/write permissions.
	 */
	public static void deleteFileOrDirectory(File file) throws IOException {
		checkNotNull(file, "file");
	}

	/**
	 * Deletes the given directory recursively.
	 *
	 * <p>If the directory does not exist, this does not throw an exception, but simply does nothing.
	 * It considers the fact that a directory-to-be-deleted is not present a success.
	 *
	 * <p>This method is safe against other concurrent deletion attempts.
	 *
	 * @param directory The directory to be deleted.
	 * @throws IOException Thrown if the given file is not a directory, or if the directory could not be
	 *                     deleted for some reason, for example due to missing access/write permissions.
	 */
	public static void deleteDirectory(File directory) throws IOException {
		checkNotNull(directory, "directory");
	}

	/**
	 * Deletes the given directory recursively, not reporting any I/O exceptions
	 * that occur.
	 *
	 * <p>This method is identical to {@link FileUtils#deleteDirectory(File)}, except that it
	 * swallows all exceptions and may leave the job quietly incomplete.
	 *
	 * @param directory The directory to delete.
	 */
	public static void deleteDirectoryQuietly(File directory) {
		if (directory == null) {
			return;
		}

		// delete and do not report if it fails
		try {
			deleteDirectory(directory);
		} catch (Exception ignored) {}
	}

	/**
	 * Removes all files contained within a directory, without removing the directory itself.
	 *
	 * <p>This method is safe against other concurrent deletion attempts.
	 *
	 * @param directory The directory to remove all files from.
	 *
	 * @throws FileNotFoundException Thrown if the directory itself does not exist.
	 * @throws IOException Thrown if the file indicates a proper file and not a directory, or if
	 *                     the directory could not be cleaned for some reason, for example
	 *                     due to missing access/write permissions.
	 */
	public static void cleanDirectory(File directory) throws IOException {
		checkNotNull(directory, "directory");
	}

	private static void deleteFileOrDirectoryInternal(File file) throws IOException {
		if (file.isDirectory()) {
			// file exists and is directory
			deleteDirectoryInternal(file);
		}
		else {
			// if the file is already gone (concurrently), we don't mind
			Files.deleteIfExists(file.toPath());
		}
		// else: already deleted
	}

	private static void deleteDirectoryInternal(File directory) throws IOException {
		if (directory.isDirectory()) {
			// directory exists and is a directory

			// empty the directory first
			try {
				cleanDirectoryInternal(directory);
			}
			catch (FileNotFoundException ignored) {
				// someone concurrently deleted the directory, nothing to do for us
				return;
			}

			// delete the directory. this fails if the directory is not empty, meaning
			// if new files got concurrently created. we want to fail then.
			// if someone else deleted the empty directory concurrently, we don't mind
			// the result is the same for us, after all
			Files.deleteIfExists(directory.toPath());
		}
		else if (directory.exists()) {
			// exists but is file, not directory
			// either an error from the caller, or concurrently a file got created
			throw new IOException(directory + " is not a directory");
		}
		// else: does not exist, which is okay (as if deleted)
	}

	private static void cleanDirectoryInternal(File directory) throws IOException {
		if (Files.isSymbolicLink(directory.toPath())) {
			// the user directories which symbolic links point to should not be cleaned.
			return;
		}
		if (directory.isDirectory()) {
			final File[] files = directory.listFiles();

			if (files == null) {
				// directory does not exist any more or no permissions
				if (directory.exists()) {
					throw new IOException("Failed to list contents of " + directory);
				} else {
					throw new FileNotFoundException(directory.toString());
				}
			}

			// remove all files in the directory
			for (File file : files) {
				if (file != null) {
					deleteFileOrDirectory(file);
				}
			}
		}
		else if (directory.exists()) {
			throw new IOException(directory + " is not a directory but a regular file");
		}
		else {
			// else does not exist at all
			throw new FileNotFoundException(directory.toString());
		}
	}


	// ------------------------------------------------------------------------
	//  Deleting directories on Flink FileSystem abstraction
	// ------------------------------------------------------------------------

	/**
	 * Deletes the path if it is empty. A path can only be empty if it is a directory which does
	 * not contain any other directories/files.
	 *
	 * @param fileSystem to use
	 * @param path to be deleted if empty
	 * @return true if the path could be deleted; otherwise false
	 * @throws IOException if the delete operation fails
	 */
	public static boolean deletePathIfEmpty(FileSystem fileSystem, Path path) throws IOException {
		// todo
		return false;
	}

	/**
	 * Copies all files from source to target and sets executable flag. Paths might be on different systems.
	 * @param sourcePath source path to copy from
	 * @param targetPath target path to copy to
	 * @param executable if target file should be executable
	 * @throws IOException if the copy fails
	 */
	public static void copy(Path sourcePath, Path targetPath, boolean executable) throws IOException {
		// we unwrap the file system to get raw streams without safety net
	}

	private static void internalCopyDirectory(Path sourcePath, Path targetPath, boolean executable, FileSystem sFS, FileSystem tFS) throws IOException {

	}

	private static void internalCopyFile(Path sourcePath, Path targetPath, boolean executable, FileSystem sFS, FileSystem tFS) throws IOException {

	}


	private static void addToZip(Path fileOrDirectory, FileSystem fs, Path rootDir, ZipOutputStream out) throws IOException {

	}


	/**
	 * List the {@code directory} recursively and return the files that satisfy the {@code fileFilter}.
	 *
	 * @param directory the directory to be listed
	 * @param fileFilter a file filter
	 * @return a collection of {@code File}s
	 *
	 * @throws IOException if an I/O error occurs while listing the files in the given directory
	 */
	public static Collection<java.nio.file.Path> listFilesInDirectory(final java.nio.file.Path directory, final Predicate<java.nio.file.Path> fileFilter) throws IOException {
		checkNotNull(directory, "directory");
		checkNotNull(fileFilter, "fileFilter");

		if (!Files.exists(directory)) {
			throw new IllegalArgumentException(String.format("The directory %s dose not exist.", directory));
		}
		if (!Files.isDirectory(directory)) {
			throw new IllegalArgumentException(String.format("The %s is not a directory.", directory));
		}

		final FilterFileVisitor filterFileVisitor = new FilterFileVisitor(fileFilter);

		Files.walkFileTree(
			directory,
			EnumSet.of(FileVisitOption.FOLLOW_LINKS),
			Integer.MAX_VALUE,
			filterFileVisitor);

		return filterFileVisitor.getFiles();
	}

	/**
	 * Relativize the given path with respect to the given base path if it is absolute.
	 *
	 * @param basePath to relativize against
	 * @param pathToRelativize path which is being relativized if it is an absolute path
	 * @return the relativized path
	 */
	public static java.nio.file.Path relativizePath(java.nio.file.Path basePath, java.nio.file.Path pathToRelativize) {
		if (pathToRelativize.isAbsolute()) {
			return basePath.relativize(pathToRelativize);
		} else {
			return pathToRelativize;
		}
	}

	/**
	 * Returns the current working directory as specified by the {@code user.dir} system property.
	 *
	 * @return current working directory
	 */
	public static java.nio.file.Path getCurrentWorkingDirectory() {
		return Paths.get(System.getProperty("user.dir"));
	}

	/**
	 * Checks whether the given file has a jar extension.
	 *
	 * @param file to check
	 * @return true if the file has a jar extension, otherwise false
	 */
	public static boolean isJarFile(java.nio.file.Path file) {
		return false;
	}

	/**
	 * Converts the given {@link java.nio.file.Path} into a file {@link URL}. The resulting url is
	 * relative iff the given path is relative.
	 *
	 * @param path to convert into a {@link URL}.
	 * @return URL
	 * @throws MalformedURLException if the path could not be converted into a file {@link URL}
	 */
	public static URL toURL(java.nio.file.Path path) throws MalformedURLException {
		final String scheme = path.toUri().getScheme();
		return new URL(scheme, null, -1, path.toString());
	}

	private static final class FilterFileVisitor extends SimpleFileVisitor<java.nio.file.Path> {

		private final Predicate<java.nio.file.Path> fileFilter;

		private final List<java.nio.file.Path> files;

		FilterFileVisitor(Predicate<java.nio.file.Path> fileFilter) {
			this.fileFilter = checkNotNull(fileFilter);
			this.files = new ArrayList<>();
		}

		@Override
		public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attrs) throws IOException {
			FileVisitResult fileVisitResult = super.visitFile(file, attrs);

			if (fileFilter.test(file)) {
				files.add(file);
			}

			return fileVisitResult;
		}

		Collection<java.nio.file.Path> getFiles() {
			return Collections.unmodifiableCollection(files);
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Private default constructor to avoid instantiation.
	 */
	private FileUtils() {}
}
