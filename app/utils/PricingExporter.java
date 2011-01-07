package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import models.Line;
import models.Pricing;
import models.Profile;
import models.Section;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import play.Logger;

public class PricingExporter {

	public int rowIndex = 0;
	
	public File export(Long pricingId) {
		Pricing pricing = Pricing.findById(pricingId);

		File file = null;
		SpreadSheet spreadSheet = null;
		try {
			file = new File("export.ods");
			
			spreadSheet = SpreadSheet.createFromFile(new File("app/data/template.ods"));
			Sheet sheet = spreadSheet.getSheet(0);
			sheet.setName(pricing.code);
			sheet.setColumnCount(50);
			sheet.setRowCount(500);
			
			sheet.getCellAt(0, 0).setStyleName("Title");
			sheet.getCellAt(0, 0).merge(pricing.profiles.size() + 3, 1);
			sheet.getColumn(0).setWidth(5);
			sheet.getColumn(1).setWidth(70);
			
			generateTitle(pricing, sheet);
			generateProfiles(pricing, sheet);
			generateLines(pricing, sheet);
			generateTotal(pricing, sheet);
			spreadSheet.saveAs(file);
		} catch (IOException e) {
			Logger.error(e, "Error during spreadsheet generation");
			return null;
		}
		return file;
	}
	
	protected void generateTitle(Pricing pricing, Sheet sheet)
	{
		sheet.getCellAt(0, rowIndex).setValue(pricing.title);
		rowIndex += 2;
	}

	protected void generateProfiles(Pricing pricing, Sheet sheet)
	{
		int col = 2;
		for (Profile profile : pricing.profiles) {
			sheet.getCellAt(col, rowIndex).setValue(profile.title);
			sheet.getCellAt(col, rowIndex + 1).setValue(profile.rate);
			col++;
		}
		rowIndex += 2;
	}
	
	protected void generateLines(Pricing pricing, Sheet sheet)
	{
		for (Section section : pricing.sections) {
			sheet.getCellAt(0, rowIndex).setValue(section.title);
			sheet.getCellAt(0, rowIndex).merge(2, 1);
			sheet.getCellAt(0, rowIndex).setStyleName("Section");
			int col = 2;
			for (Profile profile : pricing.profiles) {
				sheet.getCellAt(col, rowIndex).setValue(section.getPriceByProfile(profile));
				sheet.getCellAt(col, rowIndex).setStyleName("Section");
				col++;
			}
			sheet.getCellAt(col, rowIndex).setValue(section.getPrice());
			sheet.getCellAt(col, rowIndex).setStyleName("Section");
			rowIndex++;
			for (Line line : section.lines) {
				sheet.getCellAt(1, rowIndex).setValue(line.title);
				col = 2;
				for (Profile profile : pricing.profiles) {
					sheet.getCellAt(col, rowIndex).setValue(line.getAmountByProfile(profile));
					col++;
				}
				sheet.getCellAt(col, rowIndex).setValue(line.getPrice());
				rowIndex++;
			}
		}
	}
	
	protected void generateTotal(Pricing pricing, Sheet sheet) {
		sheet.getCellAt(0, rowIndex).setValue("Total");
		sheet.getCellAt(0, rowIndex).merge(2, 1);
		sheet.getCellAt(0, rowIndex).setStyleName("Total");
		int col = 2;
		for (Profile profile : pricing.profiles) {
			sheet.getCellAt(col, rowIndex).setValue(pricing.getAmountByProfile(profile));
			sheet.getCellAt(col, rowIndex).setStyleName("Total");
			col++;
		}
		sheet.getCellAt(col, rowIndex).setValue(pricing.getPrice());
		sheet.getCellAt(col, rowIndex).setStyleName("Total");
	}
}
