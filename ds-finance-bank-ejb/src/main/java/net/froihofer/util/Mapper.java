package net.froihofer.util;

import net.froihofer.common.data.dto.CustomerDTO;
import net.froihofer.common.data.dto.StockDTO;
import net.froihofer.data.entity.Customer;
import net.froihofer.data.entity.Stock;
import net.froihofer.dsfinance.ws.trading.PublicStockQuote;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Mapper {

    private static Customer convertCustomerDtoToCustomer(CustomerDTO customerDTO) {
        if (customerDTO == null)
            return null;
        return new Customer(
                customerDTO.getCustomerId(),
                customerDTO.getFirstName(),
                customerDTO.getLastName(),
                customerDTO.getAddress()
        );
    }

    private static CustomerDTO convertCustomerToCustomerDto(Customer customer) {
        if (customer == null)
            return null;
        return new CustomerDTO(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAddress()
        );
    }

    public static List<Stock> convertStockDtoListToStockList(List<StockDTO> list) {
        if (list == null || list.isEmpty())
            return new ArrayList<>();
        return list.stream()
                .map(stockDTO -> new Stock(
                        stockDTO.getStockId(),
                        stockDTO.getCompanyName(),
                        stockDTO.getSymbol(),
                        stockDTO.getStockExchange(),
                        stockDTO.getSharesAmount(),
                        stockDTO.getBuyingPrice(),
                        stockDTO.getCurrentTradingPrice(),
                        stockDTO.getLastTradeTime(),
                        convertCustomerDtoToCustomer(stockDTO.getOwner())))
                .collect(Collectors.toList());
    }

    public static List<StockDTO> convertStockListToStockDtoList(List<Stock> list) {
        if (list == null || list.isEmpty())
            return new ArrayList<>();
        return list.stream()
                .map(stock -> new StockDTO(
                        stock.getStockId(),
                        stock.getCompanyName(),
                        stock.getSymbol(),
                        stock.getStockExchange(),
                        stock.getSharesAmount(),
                        stock.getBuyingPrice(),
                        stock.getCurrentTradingPrice(),
                        stock.getLastTradeTime(),
                        convertCustomerToCustomerDto(stock.getOwner())))
                .collect(Collectors.toList());
    }

    public static List<CustomerDTO> convertCustomerListToCustomerDtoList(List<Customer> list) {
        if (list == null || list.isEmpty())
            return new ArrayList<>();
        return list.stream()
                .map(customer -> new CustomerDTO(
                        customer.getCustomerId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getAddress()))
                .collect(Collectors.toList());
    }

    public static List<StockDTO> convertPublicStockQuoteListToStockDtoList(List<PublicStockQuote> list){
        if (list == null || list.isEmpty())
            return new ArrayList<>();
        return list.stream()
                .map(publicStockQuote -> new StockDTO(
                        publicStockQuote.getCompanyName(),
                        publicStockQuote.getSymbol(),
                        publicStockQuote.getStockExchange(),
                        publicStockQuote.getLastTradePrice(),
                        publicStockQuote.getLastTradeTime().toGregorianCalendar().toZonedDateTime().toLocalDateTime()
                )).collect(Collectors.toList());
    }
}
