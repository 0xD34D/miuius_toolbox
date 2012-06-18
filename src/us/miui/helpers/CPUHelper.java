/**
 * 
 */
package us.miui.helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import us.miui.toolbox.RootUtils;

/**
 * @author Clark Scheff
 *
 */
public class CPUHelper {
	public static String[] getFrequencies() throws IOException {
		String[] freqs = null;
		FileInputStream fstream = 
				new FileInputStream("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = br.readLine();
		in.close();
		freqs = line.split(" ");
		int[] iFreqs = new int[freqs.length];
		for (int i = 0; i < iFreqs.length; i++)
			iFreqs[i] = Integer.parseInt(freqs[i]);
		java.util.Arrays.sort(iFreqs);
		for (int i = 0; i < iFreqs.length; i++)
			freqs[i] = Integer.toString(iFreqs[i]);
		return freqs;
	}
	
	public static String getMinFrequency() {
		String line = null;
		try {
			FileInputStream fstream = 
					new FileInputStream("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			line = br.readLine();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return line;
	}

	public static String getMaxFrequency() {
		String line = null;
		try {
			FileInputStream fstream = 
					new FileInputStream("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			line = br.readLine();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return line;
	}

	public static void setMinFrequency(String frequency) {
		try {
			RootUtils.execute("echo " + frequency + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setMaxFrequency(String frequency) {
		try {
			RootUtils.execute("echo " + frequency + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String[] getGovernors() throws IOException {
		String[] govs = null;
		FileInputStream fstream = 
				new FileInputStream("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = br.readLine();
		in.close();
		govs = line.split(" ");
		
		return govs;
	}
	
	public static String getGovernor() {
		String line = null;
		try {
			FileInputStream fstream = 
					new FileInputStream("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			line = br.readLine();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return line;
	}
	
	public static void setGovernor(String governor) {
		try {
			RootUtils.execute("echo " + governor + " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
