import React from "react";
import { useForm, Controller } from "react-hook-form";
import { TextInput, SubmitBar, LinkLabel, Dropdown} from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const SearchApplication = ({ onSearch }) => {
  const { t } = useTranslation();
  const { handleSubmit, reset, watch, register,control, formState } = useForm();
  const form = watch();
  const tenant = Digit.ULBService.getStateId();
  const { isLoading, data, isFetched } = Digit.Hooks.useCustomMDMS(
    tenant,
    "works",
    [
        {
            "name": "Department"
        }
    ]
    );
  if(data?.works){
    var { Department } = data?.works
  }
  
  const onSubmitInput = (data) => {  
    for(var key in data){
      if(data[key]=== undefined || form[key]===""){
        delete data[key]
      }
    }  
    onSearch(data);
  };

  const clearAll = () => {
    reset();
  };
  let validation = {}
  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        <div className="search-container" style={{ width: "auto", marginLeft:"24px"}}>
          <div className="search-complaint-container">
            <div className={"complaint-input-container for-pt "} style={{ width: "100%" }}>
            <div style={{margin:"5px"}}>
              <div className="filter-label" style={{ fontWeight: "normal" }}>
              {t("WORKS_LOI_ID")}
              </div>
              <TextInput 
                name="estimateNumber" 
                inputRef={register()} 
                {...(validation = {
                    isRequired: false,
                    pattern: "^[a-zA-Z0-9-_\/]*$",
                    type: "text",
                    title: t("ERR_INVALID_ESTIMATE_NO"),
                })}
              /> 
            </div>
              
            <div style={{margin:"5px"}}>
              <div className="filter-label" style={{ fontWeight: "normal" }}>
                {t("WORKS_CONT_ID")}:
              </div>
              <TextInput 
                name="contractorId" 
                inputRef={register()} 
                {...(validation = {
                    isRequired: false,
                    pattern: "^[a-zA-Z0-9-_\/]*$",
                    type: "text",
                    title: t("ERR_INVALID_CONTRACTOR_ID"),
                })}
              />
            </div>
            
            <div style={{margin:"5px"}}>
              <div className="filter-label" style={{ fontWeight: "normal" }}>
                {t("WORKS_DEPARTMENT")}:
              </div>
              <Controller
                control={control}
                name="department"
                render={(props) => (
                  <Dropdown
                    option={Department}
                    selected={props?.value}
                    optionKey={"name"}
                    t={t}
                    select={props?.onChange}
                    onBlur={props.onBlur}
                  />
                )}
              />
            </div>

            <div style={{ gridColumn: "2/3", textAlign: "right", paddingTop: "10px" }} className="input-fields">
              <LinkLabel style={{ display: "inline"}} onClick={clearAll}>
                {t("ES_COMMON_CLEAR_SEARCH")}
              </LinkLabel>
            </div>

            <div style={{ maxWidth: "unset", marginLeft: "unset" }} className="search-submit-wrapper">
              <SubmitBar
                className="submit-bar-search"
                label={t("ES_COMMON_SEARCH")}
                disabled={!!Object.keys(formState.errors).length}
                submit
              />
            </div>
            </div>
          </div>
        </div>
      </React.Fragment>
    </form>
  );
};

export default SearchApplication;