#include <iostream>
#include <cstdlib>
#include <cmath>


double vol(double v){
    int c = std::rand()%3;
    if(c==0){
        return 0;
    }
    if(c==1){
        v *=-1;
        return v;
    }
    return v;
}


int main(){
    double end_year,end_month,average_portfolio_stock_price,year,month, average_growth_percent,deposit_per_month,dividend_percent, inflation_percent, volatility_percent;// once in 20 years -50 // every 5 years -20
    std::cout<<"Plese, enter current year:"<<std::endl;
    std::cin>>year;

    std::cout<<"Plese, enter current month:"<<std::endl;
    std::cin>>month;

    std::cout<<"Plese, enter current average portfolio stock price:"<<std::endl; // write 100
    std::cin>>average_portfolio_stock_price;

    std::cout<<"Plese, enter current average growth percent:"<<std::endl;
    std::cin>>average_growth_percent;
    average_growth_percent/=100;
    average_growth_percent/=12;

    std::cout<<"Plese, enter current deposit per month:"<<std::endl; // write 1 to get in how much ou wioll get in terms of deposit
    std::cin>>deposit_per_month;

    std::cout<<"Plese, enter current dividend percent:"<<std::endl;
    std::cin>>dividend_percent;
    dividend_percent/=100;

    std::cout<<"Plese, enter current inflation percent:"<<std::endl;
    std::cin>>inflation_percent;
    inflation_percent/=100;

    std::cout<<"Plese, enter current volatility percent:"<<std::endl;
    std::cin>>volatility_percent;
    volatility_percent/=100;
    volatility_percent/=12;

    std::cout<<"Plese, enter the last year of investing:"<<std::endl;
    std::cin>>end_year;

    std::cout<<"Plese, enter last month of investing:"<<std::endl;
    std::cin>>end_month;

    double n = 1 + (end_year - year)*12 + end_month - month;

    double s = 0;
      
       
    for(int i = 0;i<n;++i){
        dividend_percent+=vol(volatility_percent);
        average_growth_percent+=vol(volatility_percent);
        inflation_percent=vol(volatility_percent);
        if(i%48==0&&i!=0){ // so that first investment is not hurt
            s*=0.8;
            average_portfolio_stock_price*=0.8;
            std::cout<<"Oh fuck correction!!! ";
        }
        if(i%240==0&&i!=0){
            s*=0.5;
            average_portfolio_stock_price*=0.5;
            std::cout<<"Oh fuck it is a crash!!!!! ";
        }
        s=s*(1+average_growth_percent+dividend_percent*(i%3==0))+deposit_per_month;
        average_portfolio_stock_price*=(1+average_growth_percent);
        s = s*(1 - (inflation_percent/12)); 
        average_portfolio_stock_price*=(1 - (inflation_percent/12));
        std::cout<<s<<" "<<average_portfolio_stock_price<<std::endl;
    }


    std::cout<<s<<" "<<average_portfolio_stock_price;
}