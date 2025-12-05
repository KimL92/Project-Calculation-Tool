package com.example.pkveksamen.model;

public enum AlphaRole {
    Developer("Developer"),
    FrontendDeveloper("Frontend Developer"),
    BackendDeveloper("Backend Developer"),
    FullstackDeveloper("Fullstack Developer"),
    Tester("Tester"),
    UXDesigner("UX Designer"),
    UIDesigner("UI Designer"),
    SolutionArchitect("Solution Architect"),
    TechnicalConsultant("Technical Consultant"),
    BusinessConsultant("Business Consultant"),
    IntegrationSpecialist("Integration Specialist"),
    EcommerceSpecialist("E-commerce Specialist"),
    CmsSpecialist("CMS Specialist"),
    PimDamSpecialist("PIM/DAM Specialist"),
    DevOpsEngineer("DevOps Engineer"),
    ProjectManager("Project Manager"),
    DataAndSearchSpecialist("Data & Search Specialist"),
    QualityAssuranceEngineer("Quality Assurance Engineer");

    private final String displayName;

    AlphaRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
