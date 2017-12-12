package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteria;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListPagination;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetProdOrgModel;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

public class AssetTestHelper extends AbstractHelper {

	/**
	 *  Creates and returns a test Asset
	 */
	public static Asset createTestAsset() throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {

		Asset asset = createDummyAsset(AssetIdType.GUID);
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(asset).getBytes()).execute().returnResponse();
		
		return checkSuccessResponseReturnObject(response, Asset.class);
	}
	
	public static Asset getAssetByGuid(String assetGuid) throws ClientProtocolException, IOException {
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/asset/" + assetGuid)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		return checkSuccessResponseReturnObject(response, Asset.class);
	}
	
	/**
	 *  Saves a given Asset
	 */
	public static Asset createAsset(Asset asset) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(asset).getBytes()).execute().returnResponse();
		
		return checkSuccessResponseReturnObject(response, Asset.class);
	}

	/**
	 *  Update Asset
	 */
	public static Asset updateAsset(Asset asset) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Put(getBaseUrl() + "asset/rest/asset?comment=UpdatedAsset")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(asset).getBytes()).execute().returnResponse();
		return checkSuccessResponseReturnObject(response, Asset.class);
	}
	
	/**
	 *  Executes a query through REST
	 */
	public static ListAssetResponse assetListQuery(AssetListQuery query) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(query).getBytes()).execute().returnResponse();
		return checkSuccessResponseReturnObject(response, ListAssetResponse.class);
	}
	
	/**
	 *  Count results from given query
	 */
	public static Integer assetListQueryCount(AssetListQuery query) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset/listcount")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(query).getBytes()).execute().returnResponse();
		return checkSuccessResponseReturnInt(response);
	}

	/**
	 *  Get Asset histories for given asset guid
	 */
	public static List<Asset> getAssetHistoryFromAssetGuid(String assetId) throws ClientProtocolException, IOException {
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/history/asset?assetId=" + assetId + "&maxNbr=100")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		return checkSuccessResponseReturnList(response, Asset.class);
	}
	
	/**
	 *  Get Asset version from history guid
	 */
	public static Asset getAssetHistoryFromHistoryGuid(String historyId) throws ClientProtocolException, IOException {
		final HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/history/" + historyId)
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		return checkSuccessResponseReturnObject(response, Asset.class);
	}
	
	public static Integer getAssetCountSweden() throws ClientProtocolException, JsonProcessingException, IOException {
		AssetListQuery assetListQuery = getBasicAssetQuery();
		AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
		assetListCriteriaPair.setKey(ConfigSearchField.FLAG_STATE);
		assetListCriteriaPair.setValue("SWE");
		assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);
		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset/listcount")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetListQuery).getBytes()).execute().returnResponse();
		return checkSuccessResponseReturnInt(response);
	}
	
	public static Asset createDummyAsset(AssetIdType assetIdType) {
		String ircs = "F" + generateARandomStringWithMaxLength(4);
		return createDummyAsset(assetIdType, ircs);
		
	}
	
	public static Asset createDummyAsset(AssetIdType assetIdType, String ircs) {
		
		Asset asset = new Asset();
		AssetId assetId = new AssetId();
		assetId.setType(assetIdType);
		switch (assetIdType) {
		case GUID:
			assetId.setGuid(UUID.randomUUID().toString());
			break;
		case INTERNAL_ID:
			assetId.setValue("INTERNALID_" + UUID.randomUUID().toString());
			break;
		}

		asset.setActive(true);
		asset.setAssetId(assetId);

		asset.setSource(CarrierSource.INTERNAL);
		// asset.setEventHistory();
		asset.setName("Ship" + generateARandomStringWithMaxLength(10));
		asset.setCountryCode("SWE");
		asset.setGearType("DERMERSAL");
		asset.setHasIrcs("1");
		asset.setIrcs(ircs);
		asset.setExternalMarking("EXT3");
		asset.setCfr("SWE0000" + ircs);

		String imo = "0" + generateARandomStringWithMaxLength(6);
		asset.setImo(imo);
		String mmsi = generateARandomStringWithMaxLength(9);
		asset.setMmsiNo(mmsi);
		asset.setHasLicense(true);
		asset.setLicenseType("MOCK-license-DB");
		asset.setHomePort("TEST_GOT");
		asset.setLengthOverAll(new BigDecimal(15.0).setScale(1));
		asset.setLengthBetweenPerpendiculars(new BigDecimal(3.0).setScale(1));
		asset.setGrossTonnage(new BigDecimal(200.0).setScale(1));

		asset.setGrossTonnageUnit("OSLO");
		asset.setSafetyGrossTonnage(new BigDecimal(80.0).setScale(1));
		asset.setPowerMain(new BigDecimal(10.0).setScale(1));
		asset.setPowerAux(new BigDecimal(10.0).setScale(1));

		AssetProdOrgModel assetProdOrgModel = new AssetProdOrgModel();
		assetProdOrgModel.setName("NAME" + generateARandomStringWithMaxLength(10));
		assetProdOrgModel.setCity("CITY" + generateARandomStringWithMaxLength(10));
		assetProdOrgModel.setAddress("ADDRESS" + generateARandomStringWithMaxLength(10));
		assetProdOrgModel.setCode("CODE" + generateARandomStringWithMaxLength(10));
		assetProdOrgModel.setPhone("070" + generateARandomStringWithMaxLength(10));
		asset.setProducer(assetProdOrgModel);
		asset.getContact();
		asset.getNotes();

		return asset;
	}
	
	public static AssetListQuery getBasicAssetQuery() {
		AssetListQuery assetListQuery = new AssetListQuery();
		AssetListPagination assetListPagination = new AssetListPagination();
		assetListPagination.setListSize(100);
		assetListPagination.setPage(1);
		assetListQuery.setPagination(assetListPagination);
		AssetListCriteria assetListCriteria = new AssetListCriteria();
		assetListCriteria.setIsDynamic(true);
		assetListQuery.setAssetSearchCriteria(assetListCriteria);
		return assetListQuery;
	}

	public static String generateARandomStringWithMaxLength(int len) {
		String ret = "";
		for (int i = 0; i < len; i++) {
			int val = new Random().nextInt(10);
			ret += String.valueOf(val);
		}
		return ret;
	}
}