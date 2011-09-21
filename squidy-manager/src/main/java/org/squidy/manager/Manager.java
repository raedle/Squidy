/**
 * 
 */
package org.squidy.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.basex.server.trigger.TriggerNotification;
import org.mvel2.MVEL;
import org.squidy.database.Session;
import org.squidy.database.SessionFactoryProvider;
import org.squidy.manager.model.ModelData;
import org.squidy.manager.model.Pipe;
import org.squidy.manager.model.Piping;
import org.squidy.manager.model.Processable;
import org.squidy.manager.model.Workspace;
import org.squidy.manager.parser.ModelHandler;
import org.squidy.resource.MessageResourceBundle;


/**
 * <code>Manager</code>.
 * 
 * <pre>
 * Date: Aug 6, 2010
 * Time: 8:00:23 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id$
 * @since 1.5.0
 */
public class Manager {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Manager.class);

	public static final String TRIGGER_PROCESSING = "processing";
	public static final String TRIGGER_PROPERTY = "property";

	public static void main(String[] args) throws Exception {
		switch (args.length) {
		case 0:
			new Manager();
			break;
		case 1:
			new Manager(args[0]);
			break;
		case 2:
			new Manager(args[0], Boolean.parseBoolean(args[1]));
			break;
		default:
			help(null);
			break;
		}
	}

	private static final Map<Command, Method> COMMANDS = getCommands(Manager.class);

	private String file;
	private ModelData data;

	private static MessageResourceBundle bundle;
	static {
		bundle = MessageResourceBundle.getBundle("manager");
	}

	private static Manager instance;

	/**
	 * 
	 */
	public static Manager get() {
		if (instance == null)
			instance = new Manager();

		return instance;
	}

	/**
	 * 
	 */
	public static Manager get(ModelData data) {
		if (instance == null)
			instance = new Manager(data);

		return instance;
	}

	private Manager() {
		Thread cmdThread = new Thread() {
			public void run() {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));

				String cmd = null;
				try {
					while ((cmd = br.readLine()) != null) {
						String[] cmdParams = cmd.split(" ");
						String cmdName = cmdParams[0];
						Object[] params = new String[cmdParams.length - 1];
						System.arraycopy(cmdParams, 1, params, 0, params.length);

						Method method = getMethodForCommandName(cmdName);

						if (method == null) {
							System.out.println(bundle.getMessage(
									"manager.unknown_command", cmdName));
							help(null);
							consoleInput();
							continue;
						}
						try {
							method.invoke(Manager.this, params);
						} catch (IllegalArgumentException e) {
							System.out.println("Illegal arguments:");
							System.out.println(bundle
									.getMessage(getCommandForCommandName(
											cmdName).help()));
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						consoleInput();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		};
		cmdThread.start();

		if (LOG.isInfoEnabled()) {
			LOG.info("Manager started");
		}
		consoleInput();
	}

	public Manager(ModelData data) {
		this();
		this.data = data;

		Session session = SessionFactoryProvider.getProvider().getSession();
		if (session != null)
			try {
				session.createTrigger(TRIGGER_PROPERTY);
				session.attachTrigger(TRIGGER_PROPERTY,
						new TriggerNotification() {

//							@Override
							public void update(String data) {
								String[] a = data.split(",");
								String[] a0 = a[0].split("=");
								String[] a1 = a[1].split("=");

								Processable p = getProcessableWithId(
										Manager.this.data.getWorkspace(), a0[1]);
								setProperty(p, a1[0], a1[1]);
							}
						});

				session.createTrigger(TRIGGER_PROCESSING);
				session.attachTrigger(TRIGGER_PROCESSING,
						new TriggerNotification() {

//							@Override
							public void update(String data) {
								String[] a = data.split(",");
								String[] a0 = a[0].split("=");
								String[] a1 = a[1].split("=");

								Processable p = getProcessableWithId(
										Manager.this.data.getWorkspace(), a0[1]);

								if (p != null)
									switch (Processable.Action.valueOf(a1[1])) {
									case START:
										p.start();
										break;
									case STOP:
										p.stop();
										break;
									case DUPLICATE:
										break;
									case DELETE:
										p.delete();
										break;
									}
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	/**
	 * @param file
	 */
	public Manager(String file) {
		this();

		load(file);
	}

	/**
	 * @param file
	 * @param autostart
	 */
	public Manager(String file, boolean autostart) {
		this(file);

		if (autostart) {
			start("*");
		}
	}

	/**
	 * 
	 */
	private static void consoleInput() {
		System.out.print("> ");
	}

	/**
	 * @param cmdName
	 * @return
	 */
	private static Command getCommandForCommandName(String cmdName) {
		for (Command cmd : COMMANDS.keySet()) {
			if (cmd.name().equals(cmdName)) {
				return cmd;
			}
		}
		return null;
	}

	/**
	 * @param cmdName
	 * @return
	 */
	private static Method getMethodForCommandName(String cmdName) {
		for (Command cmd : COMMANDS.keySet()) {
			if (cmd.name().equals(cmdName)) {
				return COMMANDS.get(cmd);
			}
		}
		return null;
	}

	/**
	 * @param file
	 */
	@Command(name = "load", description = "manager.load.description", help = "manager.load.help", order = 1)
	private void load(String file) {
		this.file = file;

		ModelHandler modelHandler = ModelHandler.getModelHandler();

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(file));
		} catch (FileNotFoundException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Could not load workspace: " + e.getMessage());
			}
		}

		if (inputStream != null) {
			data = modelHandler.load(inputStream);
			if (LOG.isInfoEnabled()) {
				LOG.info("Workspace loaded");
			}
		}
	}

	/**
	 * @throws IOException
	 */
	@Command(name = "start", description = "manager.start.description", help = "manager.start.help", order = 2)
	public void start(String id) throws ProcessException {
		if (data != null) {
			Workspace workspace = data.getWorkspace();

			if ("*".equals(id)) {
				workspace.start();
			} else {
				Processable processable = getProcessableWithId(workspace, id);
				if (processable != null) {
					processable.start();
				} else {
					System.out.println("No node with this id has been found");
				}
			}
		}
	}

	/**
	 * @throws IOException
	 */
	@Command(name = "stop", description = "manager.stop.description", help = "manager.stop.help", order = 3)
	public void stop(String id) throws ProcessException {
		if (data != null) {
			Workspace workspace = data.getWorkspace();

			if ("*".equals(id)) {
				workspace.stop();
			} else {
				Processable processable = getProcessableWithId(workspace, id);
				if (processable != null) {
					processable.stop();
				} else {
					System.out.println("No node with this id has been found");
				}
			}
		}
	}

	/**
     * 
     */
	@Command(name = "info", description = "manager.info.description", help = "manager.info.help", order = 4)
	private void info() {
		PrintStream console = System.out;

		if (data != null) {
			console.println("file:\t\t" + file);
			console.println("processing:\t"
					+ data.getWorkspace().isProcessing());
		} else {
			console.println("-");
		}
	}

	@Command(name = "list", description = "manager.list.description", help = "manager.list.help", order = 5)
	private void list(String type) {
		if ("nodes".equals(type)) {
			if (data != null) {
				Workspace workspace = data.getWorkspace();
				workspace0(workspace, 0);
			}
		}
	}

	private void workspace0(Processable processable, int tab) {
		PrintStream console = System.out;

		for (int i = 0; i < tab; i++) {
			console.print("\t");
		}

		console.println("id={" + processable.getId() + "},type={"
				+ processable.getClass().getSimpleName() + "}");
		for (Processable subProcessable : processable.getSubProcessables()) {
			workspace0(subProcessable, tab + 1);
		}
	}

	@Command(name = "help", description = "manager.help.description", help = "manager.help.help", order = 888)
	private static void help(String cmdName) {
		PrintStream console = System.out;

		if (cmdName != null) {
			String helpBundle = getCommandForCommandName(cmdName).help();
			console.println(bundle.getMessage(helpBundle));
		} else {
			for (Command cmd : COMMANDS.keySet()) {
				console.print(cmd.name());
				console.print("\t\t");
				console.print(bundle.getMessage(cmd.description()));
				console.println();
			}
		}
	}

	/**
	 * 
	 */
	@Command(name = "exit", description = "manager.exit.description", help = "manager.exit.help", order = 999)
	private void exit() {
		stop("*");

		if (LOG.isInfoEnabled()) {
			LOG.info("Manager exit");
		}

		System.exit(0);
	}

	/**
	 * @param type
	 * @return
	 */
	private static Map<Command, Method> getCommands(Class<?> type) {
		Map<Command, Method> commands = new TreeMap<Command, Method>(
				new Comparator<Command>() {

					/**
					 * @param o1
					 * @param o2
					 * @return
					 */
					public int compare(Command o1, Command o2) {
						return ((Integer) o1.order()).compareTo((Integer) o2
								.order());
					}
				});
		for (Method method : type.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				method.setAccessible(true);
				commands.put(method.getAnnotation(Command.class), method);
			}
		}

		return commands;
	}

	private static final Processable getProcessableWithId(
			Processable processable, String id) {
		if (id.equals(processable.getId())) {
			return processable;
		}

		for (Processable subProcessable : processable.getSubProcessables()) {
			Processable res = getProcessableWithId(subProcessable, id);
			if (res != null)
				return res;
		}
		
		if (processable instanceof Piping) {
			Piping piping = (Piping) processable;
			
			for (Pipe pipe : piping.getPipes()) {
				if (id.equals(pipe.getId()))
					return pipe;
			}
		}
		
		return null;
	}

	public void propertyChanged(Processable processable, String name,
			Object value) {
		try {
			setProperty(processable, name, value);
			SessionFactoryProvider.getProvider().getSession().trigger("1 to 10", TRIGGER_PROPERTY, "processable="
					+ processable.getId() + "," + name + "=" + value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setProperty(Processable processable, String name, Object value) {
		Object oldValue = MVEL.getProperty(name, processable);
		MVEL.setProperty(processable, name, value);
		Object newValue = MVEL.getProperty(name, processable);
		processable.fireStatusChange(name, oldValue, newValue);
	}

	public void notify(Processable processable, Processable.Action action) {
		try {
			SessionFactoryProvider.getProvider().getSession().trigger("1 to 10", TRIGGER_PROCESSING, "processable="
					+ processable.getId() + ",action=" + action);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * <code>Command</code>.
	 * 
	 * <pre>
	 * Date: Aug 8, 2010
	 * Time: 10:24:30 PM
	 * </pre>
	 * 
	 * @author Roman R&auml;dle, <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman
	 *         .Raedle@uni-konstanz.de</a>, University of Konstanz
	 * @version $Id$
	 * @since 1.5.0
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Command {
		String name();

		String description();

		String help();

		int order() default 0;
	}
}
